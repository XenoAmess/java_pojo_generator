package com.xenoamess.java_pojo_generator.util;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import org.jetbrains.annotations.NotNull;


/**
 *
 * https://stackoverflow.com/questions/9797212/finding-the-nearest-common-superclass-or-superinterface-of-a
 * -collection-of-cla
 *
 * @author Cephalopod
 */
public class FindJavaClassCommonParentUtil {
    @NotNull
    public static Set<Class> getSuperclasses(@NotNull Class clazz) {
        final Set<Class> result = new LinkedHashSet<>();
        final Queue<Class> queue = new ArrayDeque<>();
        queue.add(clazz);
        if (clazz.isInterface()) {
            // optional
            queue.add(Object.class);
        }
        while (!queue.isEmpty()) {
            Class c = queue.remove();
            if (result.add(c)) {
                Class sup = c.getSuperclass();
                if (sup != null) {
                    queue.add(sup);
                }
                queue.addAll(Arrays.asList(c.getInterfaces()));
            }
        }
        return result;
    }

    @NotNull
    public static Set<Class> commonSuperclasses(@NotNull Iterable<Class> classes) {
        Iterator<Class> it = classes.iterator();
        if (!it.hasNext()) {
            return Collections.emptySet();
        }
        // begin with set from first hierarchy
        Set<Class> result = getSuperclasses(it.next());
        // remove non-superclasses of remaining
        while (it.hasNext()) {
            Class c = it.next();
            Iterator<Class> resultIt = result.iterator();
            while (resultIt.hasNext()) {
                Class sup = resultIt.next();
                if (!sup.isAssignableFrom(c)) {
                    resultIt.remove();
                }
            }
        }
        return result;
    }

    @NotNull
    public static List<Class> lowestCommonSuperclasses(@NotNull Iterable<Class> classes) {
        Collection<Class> commonSupers = commonSuperclasses(classes);
        return lowestClasses(commonSupers);
    }

    @NotNull
    public static List<Class> lowestClasses(@NotNull Collection<Class> classes) {
        final LinkedList<Class> source = new LinkedList<>(classes);
        final ArrayList<Class> result = new ArrayList<>(classes.size());
        while (!source.isEmpty()) {
            Iterator<Class> srcIt = source.iterator();
            Class c = srcIt.next();
            srcIt.remove();
            while (srcIt.hasNext()) {
                Class c2 = srcIt.next();
                if (c2.isAssignableFrom(c)) {
                    srcIt.remove();
                } else if (c.isAssignableFrom(c2)) {
                    c = c2;
                    srcIt.remove();
                }
            }
            result.add(c);
        }
        result.trimToSize();
        return result;
    }
}
