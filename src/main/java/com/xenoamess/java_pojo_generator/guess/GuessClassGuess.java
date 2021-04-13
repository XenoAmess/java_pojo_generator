package com.xenoamess.java_pojo_generator.guess;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author XenoAmess
 */
public class GuessClassGuess extends AbstractClassGuess {
    private String className;

    private Map<String, FieldGuess> fields = new LinkedHashMap<>();

    public GuessClassGuess(ClassGuessManager classGuessManager) {
        super(classGuessManager);
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Map<String, FieldGuess> getFields() {
        return fields;
    }

    public void setFields(Map<String, FieldGuess> fields) {
        this.fields = fields;
    }
}
