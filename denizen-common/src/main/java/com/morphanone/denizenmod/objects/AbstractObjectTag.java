package com.morphanone.denizenmod.objects;

import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.tags.Attribute;
import com.morphanone.denizenmod.tags.TagFactories;
import com.morphanone.denizenmod.tags.factories.ObjectTagFactory;

public abstract class AbstractObjectTag implements ObjectTag {
    protected String prefix;

    public AbstractObjectTag() {
        this.prefix = factory().defaultArgPrefix();
    }

    public abstract String rawSimpleIdentity();

    public String rawIdentity() {
        return rawSimpleIdentity();
    }

    private ObjectTagFactory<?> factory() {
        return TagFactories.BY_OBJECT_TYPE.get(getClass());
    }

    @Override
    public String identifySimple() {
        return factory().objectIdentifier() + "@" + rawSimpleIdentity();
    }

    @Override
    public String identify() {
        return factory().objectIdentifier() + "@" + rawIdentity();
    }

    @Override
    public String toString() {
        return identify();
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public ObjectTag setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    @Override
    public boolean isUnique() {
        return false;
    }

    @Override
    public ObjectTag getObjectAttribute(Attribute attribute) {
        return factory().getObjectAttribute(this, attribute);
    }
}
