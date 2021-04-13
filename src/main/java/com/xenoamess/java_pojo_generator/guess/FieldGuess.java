package com.xenoamess.java_pojo_generator.guess;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author XenoAmess
 */
public class FieldGuess {

    @NotNull
    String filedName;

    @Nullable
    AbstractClassGuess fieldClass;

    public FieldGuess(@NotNull String filedName, @Nullable AbstractClassGuess fieldClass) {
        this.filedName = filedName;
        this.fieldClass = fieldClass;
    }

    @NotNull
    public String getFiledName() {
        return filedName;
    }

    public void setFiledName(@NotNull String filedName) {
        this.filedName = filedName;
    }

    @Nullable
    public AbstractClassGuess getFieldClass() {
        return fieldClass;
    }

    public void setFieldClass(@Nullable AbstractClassGuess fieldClass) {
        this.fieldClass = fieldClass;
    }
}
