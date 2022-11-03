package com.morphanone.denizenmod.utilities;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class Annotations {
    private static <T extends Annotation> T findMethodAnnotation(Class<T> annotation, Class<?> current, String name, Class<?>[] params) {
        if (current == null) {
            return null;
        }
        try {
            Method method = current.getDeclaredMethod(name, params);
            T tag = method.getAnnotation(annotation);
            if (tag != null) {
                return tag;
            }
        }
        catch (NoSuchMethodException e) {
            return null;
        }
        return findMethodAnnotation(annotation, current.getSuperclass(), name, params);
    }

    public static <T extends Annotation> T find(Method method, Class<T> annotationClass) {
        T annotation = method.getAnnotation(annotationClass);
        if (annotation != null) {
            return annotation;
        }
        Class<?> owner = method.getDeclaringClass();
        String methodName = method.getName();
        Class<?>[] params = method.getParameterTypes();
        annotation = findMethodAnnotation(annotationClass, owner.getSuperclass(), methodName, params);
        if (annotation != null) {
            return annotation;
        }
        for (Class<?> parentInterface : owner.getInterfaces()) {
            annotation = findMethodAnnotation(annotationClass, parentInterface, methodName, params);
            if (annotation != null) {
                return annotation;
            }
        }
        return null;
    }
}
