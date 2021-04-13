package com.xenoamess.java_pojo_generator;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import com.xenoamess.java_pojo_generator.guess.AbstractClassGuess;
import com.xenoamess.java_pojo_generator.guess.ClassGuessManager;
import com.xenoamess.java_pojo_generator.guess.FieldGuess;
import com.xenoamess.java_pojo_generator.guess.GuessClassGuess;
import com.xenoamess.java_pojo_generator.guess.JavaClassGuess;
import com.xenoamess.java_pojo_generator.guess.ListClassGuess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.xenoamess.java_pojo_generator.util.FindJavaClassCommonParentUtil.lowestCommonSuperclasses;

/**
 * @author XenoAmess
 */
public class GuessClassGuessGenerator {

    public <T> GuessClassGuess generate(
            final String classNameRaw,
            final Iterable<Map<String, T>> inputMaps
    ) {
        final ClassGuessManager classGuessManager = new ClassGuessManager(
                classNameRaw
        );

        GuessClassGuess res = classGuessManager.buildGuess();

        for (Map<String, T> map : inputMaps) {
            GuessClassGuess newClassGuess = continueTheGuess(classGuessManager, map);
            res = (GuessClassGuess) merge(classGuessManager, res, newClassGuess);
        }

        return res;
    }

    private <T> GuessClassGuess continueTheGuess(
            ClassGuessManager classGuessManager,
            Map<String, T> newMap
    ) {
        GuessClassGuess res = classGuessManager.buildGuess();
        for (Map.Entry<String, T> entry : newMap.entrySet()) {
            final String key = entry.getKey();
            final T value = entry.getValue();
            final AbstractClassGuess newClassGuess = getClassGuess(classGuessManager, value);
            res.getFields().put(key, new FieldGuess(key, newClassGuess));
        }
        return res;
    }

    @Nullable
    private AbstractClassGuess getClassGuess(
            @NotNull ClassGuessManager classGuessManager,
            @Nullable Object object
    ) {
        if (object == null) {
            return null;
        }
        AbstractClassGuess newClassGuess;
        if (object instanceof Map) {
            newClassGuess = continueTheGuessOfMap(
                    classGuessManager,
                    (Map) object
            );
        } else if (object instanceof Collection) {
            newClassGuess = continueTheGuessOfCollection(
                    classGuessManager,
                    (Collection) object
            );
        } else {
            newClassGuess = new JavaClassGuess<>(classGuessManager, object.getClass());
        }
        return newClassGuess;
    }

    private AbstractClassGuess continueTheGuessOfCollection(
            ClassGuessManager classGuessManager,
            Collection newCollection
    ) {
        ListClassGuess res = new ListClassGuess(classGuessManager);
        AbstractClassGuess resEntryClassGuess = null;

        for (Object entry : newCollection) {
            final AbstractClassGuess newEntryClassGuess = getClassGuess(classGuessManager, entry);
            resEntryClassGuess = merge(classGuessManager, resEntryClassGuess, newEntryClassGuess);
        }

        res.setKeyClassGuess(resEntryClassGuess);
        return res;
    }

    @NotNull
    private AbstractClassGuess continueTheGuessOfMap(
            @NotNull ClassGuessManager classGuessManager,
            Map nowMap
    ) {
        for (Object key : nowMap.keySet()) {
            if (!(key instanceof String)) {
                return new JavaClassGuess<>(classGuessManager, Map.class);
            }
        }
        return continueTheGuess(
                classGuessManager,
                nowMap
        );
    }

    @Nullable
    private AbstractClassGuess merge(
            @NotNull ClassGuessManager classGuessManager,
            @Nullable AbstractClassGuess abstractClassGuess1,
            @Nullable AbstractClassGuess abstractClassGuess2
    ) {
        if (abstractClassGuess1 == null) {
            return abstractClassGuess2;
        }
        if (abstractClassGuess2 == null) {
            return abstractClassGuess1;
        }
        if (abstractClassGuess1 instanceof JavaClassGuess && abstractClassGuess2 instanceof JavaClassGuess) {
            if (abstractClassGuess1 instanceof ListClassGuess && abstractClassGuess2 instanceof ListClassGuess) {
                ((ListClassGuess) abstractClassGuess1).setKeyClassGuess(
                        merge(
                                classGuessManager,
                                ((ListClassGuess) abstractClassGuess1).getKeyClassGuess(),
                                ((ListClassGuess) abstractClassGuess2).getKeyClassGuess()
                        )
                );
                return abstractClassGuess1;
            } else {
                JavaClassGuess javaClassGuess1 = (JavaClassGuess) abstractClassGuess1;
                JavaClassGuess javaClassGuess2 = (JavaClassGuess) abstractClassGuess2;
                Collection<Class> classes = lowestCommonSuperclasses(
                        Arrays.asList(
                                javaClassGuess1.getRealClass(),
                                javaClassGuess2.getRealClass()
                        )
                );
                Class newClass = classes.iterator().next();
                return new JavaClassGuess(classGuessManager, newClass);
            }
        } else if (abstractClassGuess1 instanceof GuessClassGuess && abstractClassGuess2 instanceof GuessClassGuess) {
            GuessClassGuess guessClassGuess1 = (GuessClassGuess) abstractClassGuess1;
            GuessClassGuess guessClassGuess2 = (GuessClassGuess) abstractClassGuess2;
            for (Map.Entry<String, FieldGuess> entry : guessClassGuess2.getFields().entrySet()) {
                FieldGuess fieldGuess1 = guessClassGuess1.getFields().get(entry.getKey());

                if (fieldGuess1 != null) {
                    AbstractClassGuess fieldClass1 = fieldGuess1.getFieldClass();
                    AbstractClassGuess fieldClass2 = entry.getValue().getFieldClass();
                    AbstractClassGuess fieldClassMerged = merge(classGuessManager, fieldClass1, fieldClass2);
                    fieldGuess1.setFieldClass(fieldClassMerged);
                    guessClassGuess1.getFields().put(entry.getKey(), fieldGuess1);
                } else {
                    guessClassGuess1.getFields().put(entry.getKey(), entry.getValue());
                }
            }
            return abstractClassGuess1;
        }
        return new JavaClassGuess(classGuessManager, Object.class);
    }

}
