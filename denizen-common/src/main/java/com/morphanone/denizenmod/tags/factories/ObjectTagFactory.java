package com.morphanone.denizenmod.tags.factories;

import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.ObjectType;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.tags.Attribute;
import com.denizenscript.denizencore.tags.ObjectTagProcessor;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.tags.TagRunnable;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.morphanone.denizenmod.tags.TagInterfaceProxy;
import com.morphanone.denizenmod.tags.annotations.GenerateTag;
import com.morphanone.denizenmod.tags.annotations.OptionalType;
import com.morphanone.denizenmod.tags.annotations.Tag;
import com.morphanone.denizenmod.utilities.Annotations;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Pattern;

public abstract class ObjectTagFactory<T extends ObjectTag> {
    public ObjectTagProcessor<T> tagProcessor = new ObjectTagProcessor<>();

    public ObjectType<T> objectType;

    public final Class<T> tagClass;

    protected final String fullIdentifier = objectIdentifier() + "@";

    public ObjectTagFactory(Class<T> tagClass) {
        this.tagClass = tagClass;
    }

    public abstract String name();

    public abstract String objectIdentifier();

    public String defaultArgPrefix() {
        return objectType.shortName;
    }

    public <R extends ObjectTag> void register(String[] names, Class<R> returnType, TagRunnable.ObjectInterface<T, R> runnable) {
        for (String name : names) {
            tagProcessor.registerTag(returnType, name, runnable);
        }
    }

    public <R extends ObjectTag, P extends ObjectTag> void register(String[] names, Class<R> returnType, Class<P> paramType, TagRunnable.ObjectWithParamInterface<T, R, P> runnable) {
        for (String name : names) {
            tagProcessor.registerTag(returnType, paramType, name, runnable);
        }
    }

    private <R extends ObjectTag> void register(String[] names, Class<R> returnType, Method method) {
        try {
            register(names, returnType, TagInterfaceProxy.object(tagClass, method, returnType));
        }
        catch (Throwable e) {
            Debug.echoError(e);
        }
    }

    private <R extends ObjectTag, P extends ObjectTag> void registerWithParam(String[] names, Class<R> returnType, Class<P> paramType, Method method) {
        try {
            register(names, returnType, paramType, TagInterfaceProxy.objectWithParam(tagClass, method, returnType, paramType));
        }
        catch (Throwable e) {
            Debug.echoError(e);
        }
    }

    private <R extends ObjectTag> void registerOptional(String[] names, Class<R> returnType, Method method) {
        try {
            register(names, returnType, TagInterfaceProxy.optionalObject(tagClass, method, returnType));
        }
        catch (Throwable e) {
            Debug.echoError(e);
        }
    }

    private void registerGenerated(String[] names, Class<?> returnType, Method method) {
        try {
            register(names, ElementTag.class, TagInterfaceProxy.generateElement(tagClass, method, returnType));
        }
        catch (Throwable e) {
            Debug.echoError(e);
        }
    }

    public boolean isCustom(Method method) {
        return false;
    }

    public <P extends ObjectTag, R extends ObjectTag> void registerCustom(String[] names, Class<R> returnType, Class<P> extraParam, Method method) {
    }

    public void registerTag(String[] names, Method method) {
        boolean isCustom = isCustom(method);
        int paramCount = method.getParameterCount();
        if (Modifier.isStatic(method.getModifiers())) {
            if (paramCount < 1 || !tagClass.isAssignableFrom(method.getParameterTypes()[0])) {
                Debug.echoError(method.getName() + " (tag " + Arrays.toString(names) + ") in " + method.getDeclaringClass().getName() + " must provide a parameter of type " + tagClass.getName());
                return;
            }
            paramCount--;
        }
        Class<?> tempReturnType;
        OptionalType optionalType = Annotations.find(method, OptionalType.class);
        if (optionalType != null) {
            tempReturnType = optionalType.value();
            if (paramCount > 0) {
                Debug.echoError(method.getName() + " (tag " + Arrays.toString(names) + ") in " + method.getDeclaringClass().getName() + " must provide no input parameters");
                return;
            }
        }
        else {
            tempReturnType = method.getReturnType();
            if (Optional.class.equals(tempReturnType)) {
                Debug.echoError(method.getName() + " (tag " + Arrays.toString(names) + ") in " + method.getDeclaringClass().getName() + " must be annotated with OptionalType to provide an Optional");
                return;
            }
        }
        if (!ObjectTag.class.isAssignableFrom(tempReturnType)) {
            Debug.echoError(method.getName() + " (tag " + Arrays.toString(names) + ") in " + method.getDeclaringClass().getName() + " must return some ObjectTag");
            return;
        }
        if (paramCount > 1) {
            Debug.echoError(method.getName() + " (tag " + Arrays.toString(names) + ") in " + method.getDeclaringClass().getName() + " must provide no more than one input parameter");
            return;
        }
        Class<? extends ObjectTag> paramType = null;
        if (paramCount == 1) {
            Class<?> tempParamType = method.getParameterTypes()[0];
            if (!ObjectTag.class.isAssignableFrom(tempParamType)) {
                Debug.echoError(method.getName() + " (tag " + Arrays.toString(names) + ") in " + method.getDeclaringClass().getName() + " must provide an input parameter of some ObjectTag");
                return;
            }
            paramType = tempParamType.asSubclass(ObjectTag.class);
        }
        Class<? extends ObjectTag> returnType = tempReturnType.asSubclass(ObjectTag.class);
        if (isCustom) {
            registerCustom(names, returnType, paramType, method);
        }
        else if (paramType != null) {
            registerWithParam(names, returnType, paramType, method);
        }
        else if (optionalType != null) {
            registerOptional(names, returnType, method);
        }
        else {
            register(names, returnType, method);
        }
    }

    public void generateTag(String[] names, Method method) {
        int paramCount = method.getParameterCount();
        if (Modifier.isStatic(method.getModifiers())) {
            if (paramCount < 1 || !tagClass.isAssignableFrom(method.getParameterTypes()[0])) {
                Debug.echoError(method.getName() + " (tag " + Arrays.toString(names) + ") in " + method.getDeclaringClass().getName() + " must provide a parameter of type " + tagClass.getName());
                return;
            }
            paramCount--;
        }
        Class<?> returnType = method.getReturnType();
        if (!TagInterfaceProxy.ELEMENT_CREATORS.containsKey(returnType)) {
            Debug.echoError(method.getName() + " (generated tag " + Arrays.toString(names) + ") in " + method.getDeclaringClass().getName() + " must return a valid type");
            return;
        }
        if (paramCount > 0) {
            Debug.echoError(method.getName() + " (generated tag " + Arrays.toString(names) + ") in " + method.getDeclaringClass().getName() + " must provide no input parameters");
            return;
        }
        registerGenerated(names, returnType, method);
    }

    private static final Pattern CAMEL_CASE = Pattern.compile("([a-z])([A-Z])");

    private static String[] fixNames(String[] names, Method method) {
        if (names.length > 0) {
            return names;
        }
        String name = method.getName();
        if (name.endsWith("Tag") && name.length() != "Tag".length()) {
            name = name.substring(0, name.length() - "Tag".length());
        }
        return new String[] { CoreUtilities.toLowerCase(CAMEL_CASE.matcher(name).replaceAll("$1_$2")) };
    }

    public static void registerTags(ObjectTagFactory<?> factory, Method[] methods) {
        for (Method method : methods) {
            Tag tag = Annotations.find(method, Tag.class);
            if (tag != null) {
                factory.registerTag(fixNames(tag.value(), method), method);
                continue;
            }
            GenerateTag generateTag = Annotations.find(method, GenerateTag.class);
            if (generateTag != null) {
                factory.generateTag(fixNames(generateTag.value(), method), method);
            }
        }
    }

    public void registerTags() {
        registerTags(this, tagClass.getMethods());
    }

    public abstract T getDefault(TagContext context);

    public abstract T valueOf(String input, TagContext context);

    public abstract boolean matches(String input);

    public String cleanInput(String input) {
        if (input == null) {
            return null;
        }
        input = CoreUtilities.toLowerCase(input);
        if (input.startsWith(fullIdentifier)) {
            input = input.substring(fullIdentifier.length());
        }
        return input;
    }

    public T cleanValueOf(String input, TagContext context) {
        return valueOf(cleanInput(input), context);
    }

    public boolean cleanMatches(String input) {
        return matches(cleanInput(input));
    }

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
