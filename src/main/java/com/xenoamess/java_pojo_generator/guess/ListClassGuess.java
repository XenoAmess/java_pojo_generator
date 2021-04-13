package com.xenoamess.java_pojo_generator.guess;

import java.util.List;

public class ListClassGuess extends JavaClassGuess {

    public ListClassGuess(ClassGuessManager classGuessManager) {
        super(classGuessManager, List.class);
    }

    private AbstractClassGuess keyClassGuess;

    public AbstractClassGuess getKeyClassGuess() {
        return keyClassGuess;
    }

    public void setKeyClassGuess(AbstractClassGuess keyClassGuess) {
        this.keyClassGuess = keyClassGuess;
    }
}
