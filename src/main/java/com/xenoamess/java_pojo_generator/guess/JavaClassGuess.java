package com.xenoamess.java_pojo_generator.guess;

/**
 * @author XenoAmess
 */
public class JavaClassGuess<T> extends AbstractClassGuess {

    private Class<T> realClass;

    public JavaClassGuess(ClassGuessManager classGuessManager, Class<T> realClass) {
        super(classGuessManager);
        this.realClass = realClass;
    }

    public Class<T> getRealClass() {
        return realClass;
    }

    public void setRealClass(Class<T> realClass) {
        this.realClass = realClass;
    }
}
