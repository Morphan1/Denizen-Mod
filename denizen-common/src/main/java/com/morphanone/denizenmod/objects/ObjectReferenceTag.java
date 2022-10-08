package com.morphanone.denizenmod.objects;

import com.denizenscript.denizencore.objects.ObjectTag;

import java.util.Optional;

public abstract class ObjectReferenceTag<T> implements ObjectTag {
    public abstract Optional<T> value();
}
