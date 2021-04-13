package com.xenoamess.java_pojo_generator;

import com.xenoamess.java_pojo_generator.guess.AbstractClassGuess;
import com.xenoamess.java_pojo_generator.guess.FieldGuess;
import com.xenoamess.java_pojo_generator.guess.GuessClassGuess;
import com.xenoamess.java_pojo_generator.guess.JavaClassGuess;
import com.xenoamess.java_pojo_generator.guess.ListClassGuess;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.CaseUtils;
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
        String className = this.getClassName(givenClassName, guessClassGuess, javaCodeBakeProperties);

        if (completedClasses.contains(className)) {
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("package ");
        stringBuilder.append(javaCodeBakeProperties.getPackageName());
        stringBuilder.append(";\n\n");

        if (javaCodeBakeProperties.isIfLombok()) {
            stringBuilder.append("@lombok.Generated\n");
            stringBuilder.append("@lombok.Data\n");
        }
        if (javaCodeBakeProperties.isIfSpringData() && javaCodeBakeProperties.isIfMongoDb()) {
            stringBuilder.append("@org.springframework.data.mongodb.core.mapping.Document\n");
        }

        // class header line
        stringBuilder.append("public class ");
        stringBuilder.append(className);
        stringBuilder.append(" {\n");

        Map<String, GuessClassGuess> nexts = new LinkedHashMap<>();

        for (FieldGuess fieldGuess : guessClassGuess.getFields().values()) {
            stringBuilder.append("\n");

            String fieldName = this.getFieldName(fieldGuess, javaCodeBakeProperties);
            if (javaCodeBakeProperties.isIfSpringData()) {
                stringBuilder.append("@org.springframework.data.mongodb.core.mapping.Field\n");
            }
            AbstractClassGuess fieldClass = fieldGuess.getFieldClass();
            if (javaCodeBakeProperties.isIfSpringData() && javaCodeBakeProperties.isIfMongoDb()) {
                if (fieldClass instanceof JavaClassGuess) {
                    Class clazz = ((JavaClassGuess<?>) fieldClass).getRealClass();
                    if (StringUtils.equals(clazz.getCanonicalName(), "org.bson.types.ObjectId")) {
                        stringBuilder.append("@org.springframework.data.annotation.Id\n");
                    }
                }
            }
            stringBuilder
                    .append("private ")
                    .append(getFieldClassName(fieldGuess, javaCodeBakeProperties))
                    .append(" ")
                    .append(fieldName)
                    .append(";\n");

            if (fieldClass instanceof ListClassGuess) {
                AbstractClassGuess childClassGuess = ((ListClassGuess) fieldClass).getKeyClassGuess();
                if (childClassGuess instanceof GuessClassGuess) {
                    nexts.put(
                            ((GuessClassGuess) childClassGuess).getClassName(),
                            (GuessClassGuess) childClassGuess
                    );
                }
            }

            if (fieldClass instanceof GuessClassGuess) {
                nexts.put(fieldName, (GuessClassGuess) fieldClass);
            }
        }

        stringBuilder
                .append("}")
                .append('\n');

        try {
            FileUtils.writeStringToFile(
                    new File(realFolder + "/" + className + ".java"),
                    stringBuilder.toString(),
                    StandardCharsets.UTF_8
            );
        } catch (IOException e) {
            throw new IllegalArgumentException("realFolder: " + realFolder + " className: " + className, e);
        }

        completedClasses.add(className);

        for (Map.Entry<String, GuessClassGuess> entry : nexts.entrySet()) {
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
            @NotNull AbstractClassGuess classGuess,
            @NotNull JavaCodeBakeProperties javaCodeBakeProperties) {

        if (classGuess instanceof ListClassGuess) {

            AbstractClassGuess childClassGuess = ((ListClassGuess) classGuess).getKeyClassGuess();
            return ((JavaClassGuess<?>) classGuess).getRealClass().getCanonicalName()
                    + "<" +
                    getClassName(
                            null,
                            childClassGuess,
                            javaCodeBakeProperties
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
            @NotNull FieldGuess fieldGuess,
            @NotNull JavaCodeBakeProperties javaCodeBakeProperties
    ) {
        AbstractClassGuess classGuess = fieldGuess.getFieldClass();

        if (classGuess instanceof ListClassGuess) {
            return getClassName(null, classGuess, javaCodeBakeProperties);
        }

        if (classGuess instanceof JavaClassGuess) {
            return getClassName(null, classGuess, javaCodeBakeProperties);
        }

        if (classGuess instanceof GuessClassGuess) {
            return javaCodeBakeProperties.getPackageName()
                    + "."
                    + getClassName(
                    fieldGuess.getFiledName(),
                    (GuessClassGuess) classGuess,
                    javaCodeBakeProperties
            );
        }

        throw new NotImplementedException("getFieldClassName for " + classGuess.getClass().getCanonicalName());
    }

    @NotNull
    private String getRealOutputFolder(@NotNull JavaCodeBakeProperties javaCodeBakeProperties) {
        String[] segs = javaCodeBakeProperties.getPackageName().split("\\.");
        StringBuilder stringBuilder = new StringBuilder(javaCodeBakeProperties.getOutputFolder());
        for (String seg : segs) {
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
}
