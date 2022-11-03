package com.morphanone.denizenmod.tags.factories;

import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.ObjectType;
import com.denizenscript.denizencore.tags.Attribute;
import com.denizenscript.denizencore.tags.ObjectTagProcessor;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.morphanone.denizenmod.tags.TagInterfaceProxy;
import com.morphanone.denizenmod.tags.Tag;
import com.morphanone.denizenmod.utilities.Annotations;

import java.lang.reflect.Method;

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

    private <R extends ObjectTag> void register(String name, Class<R> returnType, Method method) {
        try {
            tagProcessor.registerTag(returnType, name, TagInterfaceProxy.object(tagClass, method, returnType));
        }
        catch (Throwable e) {
            Debug.echoError(e);
        }
    }

    private <R extends ObjectTag, P extends ObjectTag> void registerWithParam(String name, Class<R> returnType, Class<P> paramType, Method method) {
        try {
            tagProcessor.registerTag(returnType, paramType, name, TagInterfaceProxy.objectWithParam(tagClass, method, returnType, paramType));
        }
        catch (Throwable e) {
            Debug.echoError(e);
        }
    }

    public boolean isCustom(Method method) {
        return false;
    }

    public <P extends ObjectTag, R extends ObjectTag> void registerCustom(String name, Class<R> returnType, Class<P> extraParam, Method method) {
    }

    public void registerTags() {
        for (Method method : tagClass.getMethods()) {
            Tag tag = Annotations.find(method, Tag.class);
            if (tag == null) {
                continue;
            }
            boolean isCustom = isCustom(method);
            Class<?> tempReturnType = method.getReturnType();
            if (!ObjectTag.class.isAssignableFrom(tempReturnType)) {
                Debug.echoError(method.getName() + " (tag " + tag.value() + ") in " + tagClass.getName() + " must return some ObjectTag");
                continue;
            }
            if (method.getParameterCount() > 1) {
                Debug.echoError(method.getName() + " (tag " + tag.value() + ") in " + tagClass.getName() + " must provide no more than one parameter");
                continue;
            }
            Class<? extends ObjectTag> paramType = null;
            if (method.getParameterCount() == 1) {
                Class<?> tempParamType = method.getParameterTypes()[0];
                if (!ObjectTag.class.isAssignableFrom(tempParamType)) {
                    Debug.echoError(method.getName() + " (tag " + tag.value() + ") in " + tagClass.getName() + " must provide a parameter of some ObjectTag");
                    continue;
                }
                paramType = tempParamType.asSubclass(ObjectTag.class);
            }
            Class<? extends ObjectTag> returnType = tempReturnType.asSubclass(ObjectTag.class);
            if (isCustom) {
                registerCustom(tag.value(), returnType, paramType, method);
            }
            else if (paramType != null) {
                registerWithParam(tag.value(), returnType, paramType, method);
            }
            else {
                register(tag.value(), returnType, method);
            }
        }
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
