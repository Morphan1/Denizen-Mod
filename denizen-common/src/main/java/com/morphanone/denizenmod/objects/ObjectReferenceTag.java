package com.morphanone.denizenmod.objects;

import java.util.Optional;

public interface ObjectReferenceTag<T> {
    Optional<T> value();
}
