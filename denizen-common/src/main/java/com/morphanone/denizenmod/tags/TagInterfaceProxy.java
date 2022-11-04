package com.morphanone.denizenmod.tags;

import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.tags.Attribute;
import com.denizenscript.denizencore.tags.TagRunnable;
import com.morphanone.denizenmod.utilities.OptionalFloat;

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.function.BiFunction;
import java.util.function.Function;

public class TagInterfaceProxy {
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    @FunctionalInterface
    private interface BooleanFunction<T> {
        boolean getAsBoolean(T object);
    }

    public record ObjectInterface<T extends ObjectTag, R extends ObjectTag>(Function<T, R> function) implements TagRunnable.ObjectInterface<T, R> {
        @Override
        public R run(Attribute attribute, T object) {
            return function.apply(object);
        }
    }

    public record ObjectWithParamInterface<T extends ObjectTag, R extends ObjectTag, P extends ObjectTag>(BiFunction<T, P, R> function) implements TagRunnable.ObjectWithParamInterface<T, R, P> {
        @Override
        public R run(Attribute attribute, T object, P parameter) {
            return function.apply(object, parameter);
        }
    }

    public record OptionalObject<T extends ObjectTag, R extends ObjectTag>(Function<T, Optional<R>> function) implements TagRunnable.ObjectInterface<T, R> {
        @Override
        public R run(Attribute attribute, T object) {
            return function.apply(object).orElse(null);
        }
    }

    public record GenerateElement<T extends ObjectTag, R>(Function<T, R> function, Function<R, ElementTag> creator) implements TagRunnable.ObjectInterface<T, ElementTag> {
        @Override
        public ElementTag run(Attribute attribute, T object) {
            R value = function.apply(object);
            return value != null ? creator.apply(value) : null;
        }
    }

    public record GenerateCheckedElement<T extends ObjectTag, R>(Function<T, R> function, BooleanFunction<R> checker, Function<R, ElementTag> creator) implements TagRunnable.ObjectInterface<T, ElementTag> {
        @Override
        public ElementTag run(Attribute attribute, T object) {
            R value = function.apply(object);
            return checker.getAsBoolean(value) ? creator.apply(value) : null;
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T create(MethodHandle method, MethodType methodType, Class<T> type) throws Throwable {
        methodType = methodType.wrap();
        return (T) LambdaMetafactory.metafactory(LOOKUP, "apply",
                MethodType.methodType(type),
                methodType.erase(),
                method,
                methodType
        ).getTarget().invoke();
    }

    @SuppressWarnings("unchecked")
    public static <T extends ObjectTag, R extends ObjectTag> TagRunnable.ObjectInterface<T, R> object(Class<T> tagClass, Method method, Class<R> returnType) throws Throwable {
        MethodType methodType = MethodType.methodType(returnType, tagClass);
        return new ObjectInterface<T, R>(create(LOOKUP.unreflect(method), methodType, Function.class));
    }

    @SuppressWarnings("unchecked")
    public static <T extends ObjectTag, R extends ObjectTag, P extends ObjectTag> TagRunnable.ObjectWithParamInterface<T, R, P> objectWithParam(Class<T> tagClass, Method method, Class<R> returnType, Class<P> paramType) throws Throwable {
        MethodType methodType = MethodType.methodType(returnType, tagClass, paramType);
        return new ObjectWithParamInterface<T, R, P>(create(LOOKUP.unreflect(method), methodType, BiFunction.class));
    }

    @SuppressWarnings("unchecked")
    public static <T extends ObjectTag, R extends ObjectTag> TagRunnable.ObjectInterface<T, R> optionalObject(Class<T> tagClass, Method method, Class<R> returnType) throws Throwable {
        MethodType methodType = MethodType.methodType(Optional.class, tagClass);
        return new OptionalObject<T, R>(create(LOOKUP.unreflect(method), methodType, Function.class));
    }

    @SuppressWarnings("unchecked")
    private static <T> Function<T, ElementTag> getElementConstructor(Class<T> type) {
        try {
            MethodType methodType = MethodType.methodType(ElementTag.class, type);
            return (Function<T, ElementTag>) create(LOOKUP.findConstructor(ElementTag.class, MethodType.methodType(void.class, type)), methodType, Function.class);
        }
        catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Class<?>> ELEMENT_TYPES = List.of(boolean.class, byte.class, short.class, int.class, long.class, float.class, double.class, String.class);

    public static Map<Class<?>, BooleanFunction<?>> OPTIONAL_CHECKERS = new HashMap<>();

    static {
        OPTIONAL_CHECKERS.put(OptionalDouble.class, (OptionalDouble optionalDouble) -> optionalDouble.isPresent());
        OPTIONAL_CHECKERS.put(OptionalInt.class, (OptionalInt optionalInt) -> optionalInt.isPresent());
        OPTIONAL_CHECKERS.put(OptionalFloat.class, (OptionalFloat optionalFloat) -> optionalFloat.isPresent());
        OPTIONAL_CHECKERS.put(OptionalLong.class, (OptionalLong optionalLong) -> optionalLong.isPresent());
    }

    public static Map<Class<?>, Function<?, ElementTag>> ELEMENT_CREATORS = new HashMap<>();

    static {
        for (Class<?> type : ELEMENT_TYPES) {
            ELEMENT_CREATORS.put(type, getElementConstructor(type));
        }
        ELEMENT_CREATORS.put(OptionalDouble.class, (OptionalDouble optionalDouble) -> new ElementTag(optionalDouble.orElseThrow()));
        ELEMENT_CREATORS.put(OptionalInt.class, (OptionalInt optionalInt) -> new ElementTag(optionalInt.orElseThrow()));
        ELEMENT_CREATORS.put(OptionalFloat.class, (OptionalFloat optionalFloat) -> new ElementTag(optionalFloat.orElseThrow()));
        ELEMENT_CREATORS.put(OptionalLong.class, (OptionalLong optionalLong) -> new ElementTag(optionalLong.orElseThrow()));
    }

    @SuppressWarnings("unchecked")
    public static <T extends ObjectTag, R> TagRunnable.ObjectInterface<T, ElementTag> generateElement(Class<T> tagClass, Method method, Class<R> returnType) throws Throwable {
        MethodType methodType = MethodType.methodType(returnType, tagClass);
        BooleanFunction<?> checker = OPTIONAL_CHECKERS.get(returnType);
        if (checker != null) {
            return new GenerateCheckedElement<T, R>(create(LOOKUP.unreflect(method), methodType, Function.class), (BooleanFunction<R>) checker, (Function<R, ElementTag>) ELEMENT_CREATORS.get(returnType));
        }
        return new GenerateElement<T, R>(create(LOOKUP.unreflect(method), methodType, Function.class), (Function<R, ElementTag>) ELEMENT_CREATORS.get(returnType));
    }
}
