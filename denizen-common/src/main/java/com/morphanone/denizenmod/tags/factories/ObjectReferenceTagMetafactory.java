package com.morphanone.denizenmod.tags.factories;

import com.denizenscript.denizencore.DenizenCore;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.morphanone.denizenmod.DenizenMod;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ObjectReferenceTagMetafactory<T extends ObjectTag, R> {
    public List<ObjectReferenceTagFactory<?, ?>> factories = new ArrayList<>();

    public Class<T> tagClass;

    public Class<R> referenceClass;

    public ObjectReferenceTagMetafactory(Class<T> tagClass, Class<R> referenceClass) {
        this.tagClass = tagClass;
        this.referenceClass = referenceClass;
    }

    public void register(ObjectReferenceTagFactory<?, ?> factory) {
        if (!tagClass.isAssignableFrom(factory.tagClass)) {
            Debug.echoError("Factory for " + factory.tagClass.getName() + " (" + factory.getClass().getName() + ") must create a subclass of " + tagClass.getName());
            return;
        }
        if (!referenceClass.isAssignableFrom(factory.referenceClass)) {
            Debug.echoError("Factory for " + factory.tagClass.getName() + " (" + factory.getClass().getName() + ") must reference a subclass of " + referenceClass.getName());
            return;
        }
        factories.add(0, factory);
    }

    public ObjectReferenceTagFactory<?, ?> getFactory(Class<? extends ObjectTag> factoryTagClass) {
        for (ObjectReferenceTagFactory<?, ?> factory : factories) {
            if (factory.tagClass.equals(factoryTagClass)) {
                return factory;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public T of(R object) {
        if (object == null) {
            return null;
        }
        return factories.stream().map((factory) -> (T)factory.tryOf(object))
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow();
    }
}
