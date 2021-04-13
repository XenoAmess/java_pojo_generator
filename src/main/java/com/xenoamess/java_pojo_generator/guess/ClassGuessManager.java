package com.xenoamess.java_pojo_generator.guess;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ClassGuessManager {

    private final List<AbstractClassGuess> classGuesses = new ArrayList<>();

    private final String className;

    private final AtomicInteger count = new AtomicInteger(0);

    public ClassGuessManager(String className) {
        this.className = className;
    }

    public GuessClassGuess buildGuess() {
        GuessClassGuess res = new GuessClassGuess(this);
        final int currentIndex = count.getAndIncrement();
        final String resClassName = (currentIndex == 0 ? className : className + currentIndex);
        res.setClassName(resClassName);
        classGuesses.add(res);
        return res;
    }

    public List<AbstractClassGuess> getClassGuesses() {
        return classGuesses;
    }

    public String getClassName() {
        return className;
    }

    public void kill(AbstractClassGuess abstractClassGuess) {
        this.getClassGuesses().remove(abstractClassGuess);
    }
}
