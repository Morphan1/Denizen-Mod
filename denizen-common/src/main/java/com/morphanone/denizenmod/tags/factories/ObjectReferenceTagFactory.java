package com.morphanone.denizenmod.tags.factories;

import com.denizenscript.denizencore.objects.ObjectTag;

public abstract class ObjectReferenceTagFactory<T extends ObjectTag, R> extends ObjectTagFactory<T> {
    public Class<R> referenceClass;

    public ObjectReferenceTagFactory(Class<T> tagClass, Class<R> referenceClass) {
        super(tagClass);
        this.referenceClass = referenceClass;
    }

    public T tryOf(Object obj) {
        if (referenceClass.isInstance(obj)) {
            return of(referenceClass.cast(obj));
        }
        return null;
    }

    public abstract T of(R obj);
}
