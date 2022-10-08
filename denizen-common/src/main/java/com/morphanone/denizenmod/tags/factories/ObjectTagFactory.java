package com.morphanone.denizenmod.tags.factories;

import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.tags.Attribute;
import com.denizenscript.denizencore.tags.ObjectTagProcessor;
import com.denizenscript.denizencore.tags.TagContext;

public abstract class ObjectTagFactory<T extends ObjectTag> {

    public ObjectTagProcessor<T> tagProcessor = new ObjectTagProcessor<>();

    public final Class<T> tagClass;

    public ObjectTagFactory(Class<T> tagClass) {
        this.tagClass = tagClass;
    }

    public abstract String getName();

    public abstract String getObjectIdentifier();

    public abstract void registerTags();

    public abstract T getDefault(TagContext context);

    public abstract T valueOf(String input, TagContext context);

    public abstract boolean matches(String input);

    @SuppressWarnings("unchecked")
    public T getForObject(ObjectTag objectTag, TagContext context) {
        return tagClass.isInstance(objectTag) ? (T) objectTag : valueOf(objectTag.toString(), context);
    }

    public T handleAttribute(Attribute attribute) {
        if (!attribute.hasParam()) {
            return getDefault(attribute.context);
        }
        return getForObject(attribute.getParamObject(), attribute.context);
    }
}
