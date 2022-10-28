package com.morphanone.denizenmod.tags.factories;

import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.ObjectType;
import com.denizenscript.denizencore.tags.Attribute;
import com.denizenscript.denizencore.tags.ObjectTagProcessor;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.tags.TagRunnable;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.morphanone.denizenmod.tags.Tag;

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class ObjectTagFactory<T extends ObjectTag> {
    public ObjectTagProcessor<T> tagProcessor = new ObjectTagProcessor<>();

    public ObjectType<T> objectType;

    public final Class<T> tagClass;

    public ObjectTagFactory(Class<T> tagClass) {
        this.tagClass = tagClass;
    }

    public abstract String name();

    public abstract String objectIdentifier();

    public String defaultArgPrefix() {
        return objectType.shortName;
    }

    public record ObjectInterfaceProxy<T extends ObjectTag, R extends ObjectTag>(
            Function<T, R> function
    ) implements TagRunnable.ObjectInterface<T, R> {
        @Override
        public R run(Attribute attribute, T object) {
            return function.apply(object);
        }
    }

    public record ObjectWithParamInterfaceProxy<T extends ObjectTag, R extends ObjectTag, P extends ObjectTag>(
            BiFunction<T, P, R> function
    ) implements TagRunnable.ObjectWithParamInterface<T, R, P> {
        @Override
        public R run(Attribute attribute, T object, P parameter) {
            return function.apply(object, parameter);
        }
    }

    @SuppressWarnings("unchecked")
    private <R extends ObjectTag> TagRunnable.ObjectInterface<T, R> toRunnable(Method method, Class<R> returnType) throws Throwable {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodType methodType = MethodType.methodType(returnType, tagClass);
        Function<T, R> function = (Function<T, R>) LambdaMetafactory.metafactory(lookup, "apply",
                MethodType.methodType(Function.class),
                methodType.generic(),
                lookup.unreflect(method),
                methodType
        ).getTarget().invokeExact();
        return new ObjectInterfaceProxy<>(function);
    }

    @SuppressWarnings("unchecked")
    private <R extends ObjectTag, P extends ObjectTag> TagRunnable.ObjectWithParamInterface<T, R, P> toRunnableWithParam(Method method, Class<R> returnType, Class<P> paramType) throws Throwable {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodType methodType = MethodType.methodType(returnType, paramType, tagClass);
        BiFunction<T, P, R> function = (BiFunction<T, P, R>) LambdaMetafactory.metafactory(lookup, "apply",
                MethodType.methodType(BiFunction.class),
                methodType.generic(),
                lookup.unreflect(method),
                methodType
        ).getTarget().invokeExact();
        return new ObjectWithParamInterfaceProxy<>(function);
    }

    private <R extends ObjectTag> void register(String name, Class<R> returnType, Method method) {
        try {
            method.setAccessible(true);
            tagProcessor.registerTag(returnType, name, toRunnable(method, returnType));
        }
        catch (Throwable e) {
            Debug.echoError(e);
        }
    }

    private <R extends ObjectTag, P extends ObjectTag> void register(String name, Class<R> returnType, Class<P> paramType, Method method) {
        try {
            method.setAccessible(true);
            tagProcessor.registerTag(returnType, paramType, name, toRunnableWithParam(method, returnType, paramType));
        }
        catch (Throwable e) {
            Debug.echoError(e);
        }
    }

    private static Tag findTagAnnotation(Class<?> current, String name, Class<?>[] params) {
        if (current == null) {
            return null;
        }
        try {
            Method method = current.getDeclaredMethod(name, params);
            Tag tag = method.getAnnotation(Tag.class);
            if (tag != null) {
                return tag;
            }
        }
        catch (NoSuchMethodException e) {
            return null;
        }
        return findTagAnnotation(current.getSuperclass(), name, params);
    }

    private static Tag findTagAnnotation(Method method) {
        Tag tag = method.getAnnotation(Tag.class);
        if (tag != null) {
            return tag;
        }
        Class<?> owner = method.getDeclaringClass();
        String methodName = method.getName();
        Class<?>[] params = method.getParameterTypes();
        tag = findTagAnnotation(owner.getSuperclass(), methodName, params);
        if (tag != null) {
            return tag;
        }
        for (Class<?> parentInterface : owner.getInterfaces()) {
            tag = findTagAnnotation(parentInterface, methodName, params);
            if (tag != null) {
                return tag;
            }
        }
        return null;
    }


    public void registerTags() {
        for (Method method : tagClass.getMethods()) {
            Tag tag = findTagAnnotation(method);
            if (tag == null) {
                continue;
            }
            Class<?> returnType = method.getReturnType();
            if (!ObjectTag.class.isAssignableFrom(returnType)) {
                Debug.echoError(method.getName() + " (tag " + tag.value() + ") in " + tagClass.getName() + " must return some ObjectTag");
                continue;
            }
            if (method.getParameterCount() > 1) {
                Debug.echoError(method.getName() + " (tag " + tag.value() + ") in " + tagClass.getName() + " must provide no more than one parameter");
                continue;
            }
            if (method.getParameterCount() == 1) {
                Class<?> paramType = method.getParameterTypes()[0];
                if (!ObjectTag.class.isAssignableFrom(method.getParameterTypes()[0])) {
                    Debug.echoError(method.getName() + " (tag " + tag.value() + ") in " + tagClass.getName() + " must provide a parameter of some ObjectTag");
                    continue;
                }
                register(tag.value(), returnType.asSubclass(ObjectTag.class), paramType.asSubclass(ObjectTag.class), method);
            }
            else {
                register(tag.value(), returnType.asSubclass(ObjectTag.class), method);
            }
        }
    }

    public abstract T getDefault(TagContext context);

    public abstract T valueOf(String input, TagContext context);

    public abstract boolean matches(String input);

    public boolean isReal() {
        return objectIdentifier() != null;
    }

    public T getForObject(ObjectTag objectTag, TagContext context) {
        return tagClass.isInstance(objectTag) ? tagClass.cast(objectTag) : valueOf(objectTag.toString(), context);
    }

    public T handleAttribute(Attribute attribute) {
        if (!attribute.hasParam()) {
            return getDefault(attribute.context);
        }
        return getForObject(attribute.getParamObject(), attribute.context);
    }

    public ObjectTag getObjectAttribute(ObjectTag object, Attribute attribute) {
        return tagProcessor.getObjectAttribute(tagClass.cast(object), attribute);
    }
}
