package com.xenoamess.java_pojo_generator.guess;

/**
 * @author XenoAmess
 */
public abstract class AbstractClassGuess {
    private ClassGuessManager classGuessManager;

    public AbstractClassGuess(ClassGuessManager classGuessManager) {
        this.classGuessManager = classGuessManager;
    }

    public ClassGuessManager getClassGuessManager() {
        return classGuessManager;
    }

    public void setClassGuessManager(ClassGuessManager classGuessManager) {
        this.classGuessManager = classGuessManager;
    }

    public void die(){
        this.classGuessManager.kill(this);
    }
}
