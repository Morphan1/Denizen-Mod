package com.morphanone.denizenmod.tags.factories;

import com.morphanone.denizenmod.objects.ObjectReferenceTag;

public abstract class ObjectReferenceTagFactory<T extends ObjectReferenceTag<R>, R> extends ObjectTagFactory<T> {
    public ObjectReferenceTagFactory(Class<T> tagClass) {
        super(tagClass);
    }

    public abstract T of(R obj);
}
