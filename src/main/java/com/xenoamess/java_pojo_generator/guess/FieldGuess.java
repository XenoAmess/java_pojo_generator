package com.xenoamess.java_pojo_generator.guess;

/**
 * @author XenoAmess
 */
public class FieldGuess {

    String filedName;

    AbstractClassGuess fieldClass;

    public FieldGuess(String filedName, AbstractClassGuess fieldClass) {
        this.filedName = filedName;
        this.fieldClass = fieldClass;
    }

    public String getFiledName() {
        return filedName;
    }

    public void setFiledName(String filedName) {
        this.filedName = filedName;
    }

    public AbstractClassGuess getFieldClass() {
        return fieldClass;
    }

    public void setFieldClass(AbstractClassGuess fieldClass) {
        this.fieldClass = fieldClass;
    }
}
