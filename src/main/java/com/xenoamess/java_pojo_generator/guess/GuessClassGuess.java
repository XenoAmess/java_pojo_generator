package com.xenoamess.java_pojo_generator.guess;

import java.util.LinkedHashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

/**
 * @author XenoAmess
 */
public class GuessClassGuess extends AbstractClassGuess {

    @NotNull
    private final Map<String, FieldGuess> fields = new LinkedHashMap<>();

    @NotNull
    private String className;

    private int currentIndex;

    public GuessClassGuess(@NotNull ClassGuessManager classGuessManager, @NotNull String className, int currentIndex) {
        super(classGuessManager);
        this.className = className;
        this.currentIndex = currentIndex;
    }

    @NotNull
    public String getClassName() {
        return className;
    }

    public void setClassName(@NotNull String className) {
        this.className = className;
    }

    @NotNull
    public Map<String, FieldGuess> getFields() {
        return fields;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }
}
