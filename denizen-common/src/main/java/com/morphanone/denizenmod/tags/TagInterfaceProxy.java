package com.morphanone.denizenmod.tags;

import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.tags.Attribute;
import com.denizenscript.denizencore.tags.TagRunnable;
import com.morphanone.denizenmod.objects.ObjectReferenceTag;

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.function.BiFunction;
import java.util.function.Function;

public class TagInterfaceProxy {
    public record ObjectInterface<T extends ObjectTag, R extends ObjectTag>(Function<T, R> function) implements TagRunnable.ObjectInterface<T, R> {
        @Override
        public R run(Attribute attribute, T object) {
            return function.apply(object);
        }
    }

    public record ObjectReferenceInterface<T extends ObjectReferenceTag<?>, R extends ObjectTag>(Function<T, R> function) implements TagRunnable.ObjectInterface<T, R> {
        @Override
        public R run(Attribute attribute, T object) {
            return object.value().isPresent() ? function.apply(object) : null;
        }
    }

    public record ObjectWithParamInterface<T extends ObjectTag, R extends ObjectTag, P extends ObjectTag>(BiFunction<T, P, R> function) implements TagRunnable.ObjectWithParamInterface<T, R, P> {
        @Override
        public R run(Attribute attribute, T object, P parameter) {
            return function.apply(object, parameter);
        }
    }

    public record ObjectReferenceWithParamInterface<T extends ObjectReferenceTag<?>, R extends ObjectTag, P extends ObjectTag>(BiFunction<T, P, R> function) implements TagRunnable.ObjectWithParamInterface<T, R, P> {
        @Override
        public R run(Attribute attribute, T object, P parameter) {
            return object.value().isPresent() ? function.apply(object, parameter) : null;
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T create(Method method, MethodType methodType, Class<T> type) throws Throwable {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        return (T) LambdaMetafactory.metafactory(lookup, "apply",
                MethodType.methodType(type),
                methodType.generic(),
                lookup.unreflect(method),
                methodType
        ).getTarget().invoke();
    }

    @SuppressWarnings("unchecked")
    public static <T extends ObjectTag, R extends ObjectTag> TagRunnable.ObjectInterface<T, R> object(Class<T> tagClass, Method method, Class<R> returnType) throws Throwable {
        MethodType methodType = MethodType.methodType(returnType, tagClass);
        return new ObjectInterface<T, R>(create(method, methodType, Function.class));
    }

    @SuppressWarnings("unchecked")
    public static <T extends ObjectTag, R extends ObjectTag, P extends ObjectTag> TagRunnable.ObjectWithParamInterface<T, R, P> objectWithParam(Class<T> tagClass, Method method, Class<R> returnType, Class<P> paramType) throws Throwable {
        MethodType methodType = MethodType.methodType(returnType, tagClass, paramType);
        return new ObjectWithParamInterface<T, R, P>(create(method, methodType, BiFunction.class));
    }

    /*@SuppressWarnings("unchecked")
    public static <T extends ObjectReferenceTag<?>, R extends ObjectTag> TagRunnable.ObjectInterface<T, R> objectReference(Class<T> tagClass, Method method, Class<R> returnType) throws Throwable {
        MethodType methodType = MethodType.methodType(returnType, tagClass);
        return new ObjectReferenceInterface<T, R>(create(method, methodType, Function.class));
    }

    @SuppressWarnings("unchecked")
    public static <T extends ObjectReferenceTag<?>, R extends ObjectTag, P extends ObjectTag> TagRunnable.ObjectWithParamInterface<T, R, P> objectReferenceWithParam(Class<T> tagClass, Method method, Class<R> returnType, Class<P> paramType) throws Throwable {
        MethodType methodType = MethodType.methodType(returnType, tagClass, paramType);
        return new ObjectReferenceWithParamInterface<T, R, P>(create(method, methodType, BiFunction.class));
    }*/
}
