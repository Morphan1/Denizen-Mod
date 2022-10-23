package com.morphanone.denizenmod.tags.factories;

import com.morphanone.denizenmod.objects.AbstractObjectTag;
import com.morphanone.denizenmod.objects.ObjectReferenceTag;

public abstract class ObjectReferenceTagFactory<T extends AbstractObjectTag & ObjectReferenceTag<R>, R> extends ObjectTagFactory<T> {
    public ObjectReferenceTagFactory(Class<T> tagClass) {
        super(tagClass);
    }

    public abstract T of(R obj);
}
