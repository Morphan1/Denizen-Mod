package com.morphanone.denizenmod.objects;

import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.ObjectType;
import com.denizenscript.denizencore.tags.TagContext;
import com.morphanone.denizenmod.tags.TagFactories;

import java.util.Optional;

public interface ObjectReferenceTag<T> extends ObjectTag {
    Optional<? extends T> value();

    @SuppressWarnings("unchecked")
    @Override
    default <O extends ObjectTag> O asType(ObjectType<O> objTypeData, TagContext context) {
        return TagFactories.getMetafactories(getClass()).stream()
                .map((metafactory) -> metafactory.getFactory(objTypeData.clazz))
                .findFirst()
                .flatMap((factory) -> value().map((obj) -> (O) factory.tryOf(obj)))
                .orElseGet(() -> ObjectTag.super.asType(objTypeData, context));
    }
}
