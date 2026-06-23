# Dependabot Auto-Merge 知识库

> 记录本仓库在 2026-06-23 对 Dependabot 自动合并机制做的一次完整改造，包括背景、策略、踩过的坑和最终配置。

---

## 1. 背景与问题

仓库原本的 `dependabot.yml` 只声明了 `maven` 和 `github-actions` 两个生态的更新计划，但合并完全靠人工 review 和点击 merge。这带来了几个具体问题：

| 问题 | 表现 |
| --- | --- |
| **patch / minor 更新要逐个点** | 每天几十个 PR，维护负担重 |
| **major 升级卡住** | `actions/checkout 6→7` 这类主版本号更新，原 `auto-merge.yml` 不会自动合并 |
| **没有 CI 守门** | 即便开了 auto-merge，PR 上根本没有 `pull_request` 触发的 CI，于是 `gh pr merge --auto` 找不到 required check，立即合并（典型事故：PR #470 改了 `actions/checkout` 但无人 review） |
| **required check 名字错配** | 分支保护里写的 `Java CI / build`，与 GitHub Actions 实际生成的 `build (ubuntu-latest, 11, false)` 对不上，PR 一直 `BLOCKED`（典型事故：PR #454 卡住 70 天） |
| **push / pull_request 双跑** | `build.yml` 同时监听两个事件，PR 每次更新 CI 跑两遍 |

---

## 2. 改造目标

1. patch / minor 升级：**自动合并**。
2. major 升级：**仅 github-actions 生态自动合并**（maven major 仍走人工 review，因为可能 breaking）。
3. 自动合并前必须等 CI 通过。
4. CI 不重复跑。

---

## 3. 最终配置

### 3.1 `.github/dependabot.yml`

未改动，沿用原配置即可。auto-merge 由工作流控制，dependabot 自身不需要开启 auto-merge 设置。

### 3.2 `.github/workflows/auto-merge.yml`

```yaml
name: Dependabot auto-merge

on:
  pull_request:

permissions:
  pull-requests: write
  contents: write

jobs:
  dependabot:
    if: github.event.pull_request.user.login == 'dependabot[bot]'
    runs-on: ubuntu-latest
    steps:
      - name: Dependabot metadata
        id: meta
        uses: dependabot/fetch-metadata@v2
        with:
          github-token: "${{ secrets.GITHUB_TOKEN }}"

      - name: Check if auto-merge applicable
        id: check
        run: |
          TYPE="${{ steps.meta.outputs.update-type }}"
          REF="${{ github.event.pull_request.head.ref }}"
          if [[ "$TYPE" == "version-update:semver-patch" ]] || \
             [[ "$TYPE" == "version-update:semver-minor" ]] || \
             { [[ "$TYPE" == "version-update:semver-major" ]] && [[ "$REF" == dependabot/github_actions/* ]]; }; then
            echo "should_merge=true" >> $GITHUB_OUTPUT
          else
            echo "should_merge=false" >> $GITHUB_OUTPUT
          fi

      - name: Approve dependabot PR
        if: steps.check.outputs.should_merge == 'true'
        run: gh pr review --approve "$PR_URL"
        env:
          PR_URL: ${{ github.event.pull_request.html_url }}
          GH_TOKEN: ${{ secrets.GITHUB_TOKEN }}

      - name: Enable auto-merge
        if: steps.check.outputs.should_merge == 'true'
        run: gh pr merge --auto --rebase "$PR_URL"
        env:
          PR_URL: ${{ github.event.pull_request.html_url }}
          GH_TOKEN: ${{ secrets.GITHUB_TOKEN }}
```

判定规则：

| `update-type` | head ref 前缀 | 是否合并 |
| --- | --- | --- |
| semver-patch | 任意 | 是 |
| semver-minor | 任意 | 是 |
| semver-major | `dependabot/github_actions/*` | 是 |
| semver-major | 其他（maven 等） | 否，留人工 review |
| 其他（digest / indirect 等） | 任意 | 否 |

### 3.3 `.github/workflows/build.yml`

```yaml
name: Java CI

on:
  push:
    branches: [ master ]
  pull_request:

jobs:
  build:
    runs-on: ${{ matrix.os }}
    continue-on-error: ${{ matrix.experimental }}
    strategy:
      matrix:
        os: [ ubuntu-latest , windows-latest ]
        java: [ 11 ]
        experimental: [ false ]

    steps:
      - uses: actions/checkout@v6
      - uses: actions/cache@v5
        with:
          path: ~/.m2/repository
          key: ${{ runner.os }}-maven-${{ hashFiles('**/pom.xml') }}
          restore-keys: |
            ${{ runner.os }}-maven-
      - name: Set up JDK ${{ matrix.java }}
        uses: actions/setup-java@v5
        with:
          java-version: ${{ matrix.java }}
          distribution: adopt
      - name: Build with mvnw
        run: |
          chmod 777 ./mvnw
          ./mvnw install
```

要点：

- `push` 限定 `master` → 合并到 master 时跑一次。
- `pull_request` 不限定分支 → 任意 PR 触发一次。
- 两者**不重叠**：dependabot 推到 PR 分支只触发 `pull_request` 事件，不会再触发 `push`。

### 3.4 分支保护

通过 GitHub REST API 配置 `master` 分支保护：

```bash
gh api -X PUT repos/<owner>/<repo>/branches/master/protection \
  -H "Accept: application/vnd.github+json" \
  --input - <<'EOF'
{
  "required_status_checks": {
    "strict": true,
    "checks": [
      { "context": "build (ubuntu-latest, 11, false)" },
      { "context": "build (windows-latest, 11, false)" }
    ]
  },
  "enforce_admins": false,
  "required_pull_request_reviews": null,
  "restrictions": null
}
EOF
```

| 字段 | 取值 | 含义 |
| --- | --- | --- |
| `strict` | `true` | PR 必须与 base 分支同步才能合并 |
| `checks[].context` | `build (ubuntu-latest, 11, false)` / `build (windows-latest, 11, false)` | 必填：job name 加 matrix 维度 |
| `enforce_admins` | `false` | 管理员可绕过 |
| `required_pull_request_reviews` | `null` | 不强制人工 review（auto-merge 不需要） |

---

## 4. 踩过的坑

### 4.1 major 升级没合并（PR #470）

**症状**：`actions/checkout 6→7` 这种 `version-update:semver-major` PR 一直不合并。

**原因**：原 `auto-merge.yml` 只允许 `semver-patch` 和 `semver-minor`。

**解决**：规则里加上 `semver-major`，但只对 `dependabot/github_actions/*` head ref 生效（见 §3.2 的判定表）。maven major 不放开，避免引入 breaking change。

### 4.2 自动合并没等 CI（PR #470 事故）

**症状**：CI 根本没跑，PR 就被合并了。

**原因**：`build.yml` 只监听 `push` 事件。Dependabot PR 上没有 `Java CI` 状态，`gh pr merge --auto` 找不到 required check（此时还没有 required check），直接合并。

**解决**：
1. `build.yml` 加上 `pull_request:` 触发器。
2. 仓库设置里把 `Java CI` 设为 required status check（见 §3.4）。

### 4.3 required check 名字对不上（PR #454 卡 70 天）

**症状**：CI 全绿，auto-merge 已开启，但 `mergeStateStatus: "BLOCKED"`，等了好几个月没合并。

**原因**：分支保护里写的 required check 是 `Java CI / build`，但 `build.yml` 的 job 因为有 matrix 维度，GitHub Actions 实际生成的名字是 `build (ubuntu-latest, 11, false)` 和 `build (windows-latest, 11, false)`。用 GraphQL 查询时这两个 check 的 `isRequired` 字段都是 `false`。

**解决**：把 §3.4 里的 `context` 改成 `build (ubuntu-latest, 11, false)` / `build (windows-latest, 11, false)`。查询命令：

```bash
gh api graphql -F query='
query {
  repository(owner: "<owner>", name: "<repo>") {
    pullRequest(number: <N>) {
      statusCheckRollup {
        contexts(first: 20) {
          nodes {
            ... on CheckRun {
              name
              isRequired(pullRequestNumber: <N>)
            }
          }
        }
      }
    }
  }
}'
```

**经验**：`isRequired(pullRequestNumber:)` 是判断 required check 配错的最快手段，看到 `false` 就说明名字没对上。

### 4.4 push / pull_request 双跑（PR #471）

**症状**：每个 dependabot PR 触发 4 个 build（ubuntu push、ubuntu PR、windows push、windows PR），浪费 runner。

**原因**：`build.yml` 同时写了 `push:` 和 `pull_request:`，依赖 bot 推一次 commit 同时触发两个事件。

**解决**：把 `push` 限定为只对 `master` 分支生效（见 §3.3）。这样 PR 阶段只走 `pull_request`，合并到 master 后只走 `push`。

---

## 5. 验证清单

改造完成后，可以用以下步骤自检：

1. **CI 在 PR 上跑**
   - 提一个测试 PR，期望看到 `build (ubuntu-latest, 11, false)` 和 `build (windows-latest, 11, false)` 两个状态。
2. **required check 匹配**
   ```bash
   gh api graphql ...   # 见 §4.3 的查询
   ```
   期望 `isRequired: true`。
3. **patch 自动合并**
   - 故意制造一个 patch 级别更新（例如 `actions/cache` patch bump），期望 PR 被自动批准并合并。
4. **major（github-actions）自动合并**
   - 制造一个 major github-actions 更新，期望自动合并。
5. **major（maven）不自动合并**
   - 制造一个 maven major 更新，期望 PR 留在 open 状态等人工 review。
6. **CI 不双跑**
   - 在 Actions 页面看 dependabot PR 的 runs，期望每个 matrix 维度只有 1 个 run。

---

## 6. 关键 commit

| commit | 说明 |
| --- | --- |
| `2d3778e` | `auto-merge.yml` 放开 github-actions 的 major 升级 |
| `797291b` | `build.yml` 加上 `pull_request:` 触发器 |
| `dcbdce1` | `build.yml` 把 `push` 限定为只对 `master` 触发，避免双跑 |
| 分支保护设置 | `required_status_checks.checks` 写实际的 matrix job name |
