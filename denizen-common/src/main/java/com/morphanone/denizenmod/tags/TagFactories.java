package com.morphanone.denizenmod.tags;

import com.denizenscript.denizencore.objects.Adjustable;
import com.denizenscript.denizencore.objects.ObjectFetcher;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.ObjectType;
import com.denizenscript.denizencore.tags.CoreObjectTags;
import com.denizenscript.denizencore.tags.ObjectTagProcessor;
import com.denizenscript.denizencore.tags.TagManager;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import com.denizenscript.denizencore.utilities.debugging.DebugInternals;
import com.morphanone.denizenmod.objects.PlayerTag;
import com.morphanone.denizenmod.tags.factories.ObjectReferenceTagFactory;
import com.morphanone.denizenmod.tags.factories.ObjectTagFactory;
import com.morphanone.denizenmod.tags.factories.PlayerTagFactory;
import net.minecraft.world.entity.player.Player;

public class TagFactories {
    public static ObjectReferenceTagFactory<PlayerTag, Player> PLAYER = registerTagFactory(new PlayerTagFactory());

    public static ObjectTagFactory<?> bootstrap() {
        return PLAYER;
    }

    public static <T extends ObjectTag> ObjectType<T> registerWithObjectFetcher(ObjectTagFactory<T> baseTag, String shortName, String longName) {
        Class<T> objectTag = baseTag.tagClass;
        ObjectTagProcessor<T> processor = baseTag.tagProcessor;
        ObjectType<T> newType = new ObjectType<>();
        newType.clazz = objectTag;
        boolean isRealObject = !baseTag.getClass().equals(objectTag);
        if (processor != null) {
            processor.type = objectTag;
            if (isRealObject) {
                CoreObjectTags.generateCoreTags(processor);
            }
            newType.tagProcessor = processor;
        }
        newType.longName = longName;
        newType.shortName = shortName;
        newType.isAdjustable = Adjustable.class.isAssignableFrom(objectTag);
        ObjectFetcher.objectsByClass.put(objectTag, newType);
        String identifier;
        ObjectType.MatchesInterface matches;
        ObjectType.ValueOfInterface<T> valueOf;
        identifier = baseTag.getObjectIdentifier();
        matches = baseTag::matches;
        valueOf = baseTag::valueOf;
        baseTag.registerTags();
        if (isRealObject) {
            ObjectFetcher.realObjectClassSet.add(objectTag);
            ObjectFetcher.objectsByPrefix.put(CoreUtilities.toLowerCase(identifier.trim()), newType);
            ObjectFetcher.objectsByName.put(CoreUtilities.toLowerCase(longName), newType);
            if (shortName != null) {
                ObjectFetcher.objectsByName.put(CoreUtilities.toLowerCase(shortName), newType);
            }
            newType.prefix = identifier;
        }
        newType.matches = matches;
        newType.valueOf = valueOf;
        return newType;
    }

    public static <T extends ObjectTag> void registerWithObjectFetcher(ObjectTagFactory<T> tagBase) {
        String longName = DebugInternals.getClassNameOpti(tagBase.tagClass);
        String shortName = null;
        if (longName.endsWith("Tag")) {
            shortName = longName.substring(0, longName.length() - "Tag".length());
        }
        registerWithObjectFetcher(tagBase, shortName, longName);
    }

    public static <T extends ObjectTag> void registerWithTagManager(ObjectTagFactory<T> tagBase, boolean isStatic) {
        TagManager.internalRegisterTagHandler(tagBase.tagClass, tagBase.getName(), tagBase::handleAttribute, isStatic);
    }

    public static <T extends ObjectTag, F extends ObjectTagFactory<T>> F registerTagFactory(F factory) {
        registerWithObjectFetcher(factory);
        registerWithTagManager(factory, false);
        return factory;
    }

    public static <T extends ObjectTag, F extends ObjectTagFactory<T>> F registerStaticTagFactory(F factory) {
        registerWithObjectFetcher(factory);
        registerWithTagManager(factory, true);
        return factory;
    }
}
