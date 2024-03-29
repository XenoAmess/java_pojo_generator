package com.xenoamess.java_pojo_generator;

import com.xenoamess.java_pojo_generator.guess.AbstractClassGuess;
import com.xenoamess.java_pojo_generator.guess.FieldGuess;
import com.xenoamess.java_pojo_generator.guess.GuessClassGuess;
import com.xenoamess.java_pojo_generator.guess.JavaClassGuess;
import com.xenoamess.java_pojo_generator.guess.ListClassGuess;
import com.xenoamess.java_pojo_generator.util.CaseUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class JavaFilesBaker {
    public void bake(
            @NotNull GuessClassGuess guessClassGuess,
            @NotNull JavaCodeBakeProperties javaCodeBakeProperties
    ) {
        assertLegal(javaCodeBakeProperties);

        String realFolder = getRealOutputFolder(
                javaCodeBakeProperties
        );

        bake(
                guessClassGuess.getClassName(),
                guessClassGuess,
                realFolder,
                javaCodeBakeProperties,
                new HashSet<>()
        );

    }

    private void bake(
            @Nullable String givenClassName,
            @NotNull GuessClassGuess guessClassGuess,
            @NotNull String realFolder,
            @NotNull JavaCodeBakeProperties javaCodeBakeProperties,
            @NotNull Set<String> completedClasses
    ) {
        LinkedHashSet<String> imports = new LinkedHashSet<>();

        String className = this.getClassName(
                givenClassName,
                guessClassGuess,
                javaCodeBakeProperties,
                imports
        );


        if (completedClasses.contains(className)) {
            return;
        }

        StringBuilder stringBuilderBody = new StringBuilder();

        if (javaCodeBakeProperties.isIfLombok()) {
            if (javaCodeBakeProperties.isIfMarkLombokGenerated()) {
                stringBuilderBody
                        .append("@")
                        .append(
                                registerClassName(
                                        "lombok.Generated",
                                        javaCodeBakeProperties,
                                        imports
                                )
                        )
                        .append("\n");
            }
            stringBuilderBody
                    .append("@")
                    .append(
                            registerClassName(
                                    "lombok.Data",
                                    javaCodeBakeProperties,
                                    imports
                            )
                    )
                    .append("\n");
        }

        if (guessClassGuess.getCurrentIndex() == 0) {
            if (javaCodeBakeProperties.isIfSpringData() && javaCodeBakeProperties.isIfMongoDb()) {
                stringBuilderBody
                        .append("@")
                        .append(
                                registerClassName(
                                        "org.springframework.data.mongodb.core.mapping.Document",
                                        javaCodeBakeProperties,
                                        imports
                                )
                        )
                        .append("(\"")
                        .append(givenClassName)
                        .append("\")\n");
            }
        }

        // class header line
        stringBuilderBody.append("public class ");
        stringBuilderBody.append(className);
        stringBuilderBody.append(" {\n");

        Map<String, GuessClassGuess> nextGuessClassGuesses = new LinkedHashMap<>();

        for (FieldGuess fieldGuess : guessClassGuess.getFields().values()) {
            stringBuilderBody.append("\n");

            String fieldName = this.getFieldName(fieldGuess, javaCodeBakeProperties);
            if (javaCodeBakeProperties.isIfSpringData()) {
                stringBuilderBody
                        .append("    ")
                        .append("@")
                        .append(
                                registerClassName(
                                        "org.springframework.data.mongodb.core.mapping.Field",
                                        javaCodeBakeProperties,
                                        imports
                                )
                        )
                        .append("(\"")
                        .append(fieldGuess.getFiledName())
                        .append("\")\n");
            }
            if (javaCodeBakeProperties.isIfJackson()) {
                stringBuilderBody
                        .append("    ")
                        .append("@")
                        .append(
                                registerClassName(
                                        "com.fasterxml.jackson.annotation.JsonProperty",
                                        javaCodeBakeProperties,
                                        imports
                                )
                        )
                        .append("(\"")
                        .append(fieldGuess.getFiledName())
                        .append("\")\n");
            }
            if (javaCodeBakeProperties.isIfFastJson()) {
                stringBuilderBody
                        .append("    ")
                        .append("@")
                        .append(
                                registerClassName(
                                        "com.alibaba.fastjson.annotation.JSONField",
                                        javaCodeBakeProperties,
                                        imports
                                )
                        )
                        .append("(name=\"")
                        .append(fieldGuess.getFiledName())
                        .append("\")\n");
            }
            AbstractClassGuess fieldClass = fieldGuess.getFieldClass();
            if (javaCodeBakeProperties.isIfSpringData() && javaCodeBakeProperties.isIfMongoDb()) {
                if (StringUtils.equals("_id", fieldGuess.getFiledName())) {
                    stringBuilderBody
                            .append("    ")
                            .append("@")
                            .append(
                                    registerClassName(
                                            "org.springframework.data.annotation.Id",
                                            javaCodeBakeProperties,
                                            imports
                                    )
                            )
                            .append("\n");
                }
            }
            stringBuilderBody
                    .append("    ")
                    .append("private ")
                    .append(
                            registerClassName(
                                    getFieldClassName(
                                            className,
                                            fieldGuess,
                                            javaCodeBakeProperties,
                                            imports
                                    ),
                                    javaCodeBakeProperties,
                                    imports
                            )
                    )
                    .append(" ")
                    .append(fieldName)
                    .append(";\n");

            if (fieldClass instanceof ListClassGuess) {
                AbstractClassGuess childClassGuess = ((ListClassGuess) fieldClass).getKeyClassGuess();
                if (childClassGuess instanceof GuessClassGuess) {
                    final String childClassName;
                    if (javaCodeBakeProperties.isIfUsingAddOverlayName()) {
                        childClassName = className + CaseUtils.toCamelCase(
                                fieldName,
                                true,
                                ' ', '\t', '_', '.', '-'
                        ) + "Dto";
                    } else {
                        childClassName = ((GuessClassGuess) childClassGuess).getClassName();
                    }
                    nextGuessClassGuesses.put(
                            childClassName,
                            (GuessClassGuess) childClassGuess
                    );
                }
            }

            if (fieldClass instanceof GuessClassGuess) {
                final String childClassName;
                if (javaCodeBakeProperties.isIfUsingAddOverlayName()) {
                    childClassName = className + CaseUtils.toCamelCase(
                            fieldName,
                            true,
                            ' ', '\t', '_', '.', '-'
                    ) + "Dto";
                } else {
                    childClassName = fieldName;
                }
                nextGuessClassGuesses.put(
                        childClassName,
                        (GuessClassGuess) fieldClass
                );
            }
        }

        stringBuilderBody
                .append("}")
                .append('\n');

        StringBuilder stringBuilderFull = new StringBuilder();

        stringBuilderFull.append("package ");
        stringBuilderFull.append(javaCodeBakeProperties.getPackageName());
        stringBuilderFull.append(";\n\n");

        if (javaCodeBakeProperties.isIfUsingImports()) {
            for (String importedClass : imports) {
                if (importedClass.split("\\.").length != 1) {
                    stringBuilderFull
                            .append("import ")
                            .append(importedClass)
                            .append(";\n");
                }
            }
            stringBuilderFull.append("\n");
        }

        stringBuilderFull.append(stringBuilderBody);

        try {
            FileUtils.writeStringToFile(
                    new File(realFolder + "/" + className + ".java"),
                    stringBuilderFull.toString(),
                    StandardCharsets.UTF_8
            );
        } catch (IOException e) {
            throw new IllegalArgumentException("realFolder: " + realFolder + " className: " + className, e);
        }

        completedClasses.add(className);

        for (Map.Entry<String, GuessClassGuess> entry : nextGuessClassGuesses.entrySet()) {
            bake(
                    entry.getKey(),
                    entry.getValue(),
                    realFolder,
                    javaCodeBakeProperties,
                    completedClasses
            );
        }
    }


    @NotNull
    private String getClassName(
            @Nullable String givenClassName,
            @Nullable AbstractClassGuess classGuess,
            @NotNull JavaCodeBakeProperties javaCodeBakeProperties,
            @NotNull LinkedHashSet<String> imports
    ) {

        if (classGuess == null) {
            return "java.lang.Void";
        }

        if (classGuess instanceof ListClassGuess) {

            AbstractClassGuess childClassGuess = ((ListClassGuess) classGuess).getKeyClassGuess();
            String innerClassName;
            if (givenClassName != null) {
                innerClassName = CaseUtils.toCamelCase(
                        givenClassName,
                        true,
                        ' ', '\t', '_', '.', '-'
                );
            } else {
                innerClassName = getClassName(
                        null,
                        childClassGuess,
                        javaCodeBakeProperties,
                        imports
                );
            }
            return ((JavaClassGuess<?>) classGuess).getRealClass().getCanonicalName()
                    + "<" +
                    registerClassName(
                            innerClassName,
                            javaCodeBakeProperties,
                            imports
                    )
                    + ">";
        }

        if (classGuess instanceof JavaClassGuess) {
            return ((JavaClassGuess<?>) classGuess).getRealClass().getCanonicalName();
        }

        if (classGuess instanceof GuessClassGuess) {
            if (!javaCodeBakeProperties.isIfBeautify()) {
                return ((GuessClassGuess) classGuess).getClassName();
            } else {
                if (givenClassName != null) {
                    return CaseUtils.toCamelCase(
                            givenClassName,
                            true,
                            ' ', '\t', '_', '.', '-'
                    );
                } else {
                    return CaseUtils.toCamelCase(
                            ((GuessClassGuess) classGuess).getClassName(),
                            true,
                            ' ', '\t', '_', '.', '-'
                    );
                }
            }
        }

        throw new NotImplementedException("getFieldClassName for " + classGuess.getClass().getCanonicalName());
    }

    @NotNull
    private String getFieldName(
            @NotNull FieldGuess fieldGuess,
            @NotNull JavaCodeBakeProperties javaCodeBakeProperties
    ) {
        if (!javaCodeBakeProperties.isIfBeautify()) {
            return fieldGuess.getFiledName()
                    .replace(' ', '_')
                    .replace('\t', '_');
        } else {
            return CaseUtils.toCamelCase(
                    fieldGuess.getFiledName(),
                    false,
                    ' ', '\t', '_', '.', '-'
            );
        }
    }

    @NotNull
    private String getFieldClassName(
            @NotNull String className,
            @NotNull FieldGuess fieldGuess,
            @NotNull JavaCodeBakeProperties javaCodeBakeProperties,
            @NotNull LinkedHashSet<String> imports
    ) {
        AbstractClassGuess classGuess = fieldGuess.getFieldClass();

        if (classGuess == null) {
            return getClassName(null, classGuess, javaCodeBakeProperties, imports);
        }

        if (classGuess instanceof ListClassGuess) {
            final String childClassName;
            if (javaCodeBakeProperties.isIfUsingAddOverlayName()) {
                childClassName = className + CaseUtils.toCamelCase(
                        fieldGuess.getFiledName(),
                        true,
                        ' ', '\t', '_', '.', '-'
                ) + "Dto";
            } else {
                childClassName = null;
            }
            return getClassName(childClassName, classGuess, javaCodeBakeProperties, imports);
        }

        if (classGuess instanceof JavaClassGuess) {
            return getClassName(null, classGuess, javaCodeBakeProperties, imports);
        }

        if (classGuess instanceof GuessClassGuess) {
            final String childClassName;
            if (javaCodeBakeProperties.isIfUsingAddOverlayName()) {
                childClassName = className + CaseUtils.toCamelCase(
                        fieldGuess.getFiledName(),
                        true,
                        ' ', '\t', '_', '.', '-'
                ) + "Dto";
            } else {
                childClassName = fieldGuess.getFiledName();
            }
            return javaCodeBakeProperties.getPackageName()
                    + "."
                    + getClassName(
                    childClassName,
                    classGuess,
                    javaCodeBakeProperties,
                    imports
            );
        }

        throw new NotImplementedException("getFieldClassName for " + classGuess.getClass().getCanonicalName());
    }

    @NotNull
    private String getRealOutputFolder(@NotNull JavaCodeBakeProperties javaCodeBakeProperties) {
        String[] split = javaCodeBakeProperties.getPackageName().split("\\.");
        StringBuilder stringBuilder = new StringBuilder(javaCodeBakeProperties.getOutputFolder());
        for (String seg : split) {
            stringBuilder.append('/');
            stringBuilder.append(seg);
        }
        return stringBuilder.toString();
    }

    private void assertLegal(JavaCodeBakeProperties javaCodeBakeProperties) {
        if (!javaCodeBakeProperties.isIfLombok()) {
            throw new NotImplementedException(
                    "sorry but lombok is really needed in this version... " +
                            "It will be optional in future, I promise.");
        }
    }

    @NotNull
    protected String registerClassName(
            @NotNull String fullClassName,
            @NotNull JavaCodeBakeProperties javaCodeBakeProperties,
            @NotNull LinkedHashSet<String> imports
    ) {
        String registeredClassName = fullClassName;
        if (fullClassName.contains("<")) {
            registeredClassName = fullClassName.split("<")[0];
        }
        imports.add(registeredClassName);
        if (!javaCodeBakeProperties.isIfUsingImports()) {
            return fullClassName;
        } else {
            String[] split = fullClassName.split("\\.");
            return split[split.length - 1];
        }
    }
}
