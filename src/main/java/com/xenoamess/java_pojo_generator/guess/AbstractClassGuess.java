package com.xenoamess.java_pojo_generator.guess;

import org.jetbrains.annotations.NotNull;

/**
 * @author XenoAmess
 */
public abstract class AbstractClassGuess {

    @NotNull
    private ClassGuessManager classGuessManager;

    public AbstractClassGuess(@NotNull ClassGuessManager classGuessManager) {
        this.classGuessManager = classGuessManager;
    }

    @NotNull
    public ClassGuessManager getClassGuessManager() {
        return classGuessManager;
    }

    public void setClassGuessManager(@NotNull ClassGuessManager classGuessManager) {
        this.classGuessManager = classGuessManager;
    }

    public void die(){
        this.classGuessManager.kill(this);
    }
}
