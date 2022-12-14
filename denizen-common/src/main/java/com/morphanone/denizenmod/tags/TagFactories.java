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
import com.morphanone.denizenmod.objects.EntityTag;
import com.morphanone.denizenmod.objects.EntityTagImpl;
import com.morphanone.denizenmod.objects.LocationTag;
import com.morphanone.denizenmod.objects.PlayerTag;
import com.morphanone.denizenmod.objects.WorldTag;
import com.morphanone.denizenmod.tags.factories.EntityTagFactory;
import com.morphanone.denizenmod.tags.factories.LocationTagFactory;
import com.morphanone.denizenmod.tags.factories.ObjectReferenceTagFactory;
import com.morphanone.denizenmod.tags.factories.ObjectReferenceTagMetafactory;
import com.morphanone.denizenmod.tags.factories.ObjectTagFactory;
import com.morphanone.denizenmod.tags.factories.PlayerTagFactory;
import com.morphanone.denizenmod.tags.factories.WorldTagFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TagFactories {
    public static final Map<Class<? extends ObjectTag>, ObjectReferenceTagMetafactory<?, ?>> METAFACTORIES = new HashMap<>();

    public static final Map<Class<? extends ObjectTag>, ObjectTagFactory<?>> BY_OBJECT_TYPE = new HashMap<>();

    public static ObjectReferenceTagMetafactory<EntityTag, Entity> ENTITY_ANY = registerMetafactory(new ObjectReferenceTagMetafactory<>(EntityTag.class, Entity.class));

    public static ObjectReferenceTagFactory<EntityTagImpl, Entity> ENTITY = registerTagFactory(new EntityTagFactory.EntityImpl());

    public static ObjectReferenceTagFactory<PlayerTag, Player> PLAYER = registerTagFactory(new PlayerTagFactory());

    public static ObjectReferenceTagFactory<WorldTag, Level> WORLD = registerTagFactory(new WorldTagFactory());

    public static ObjectTagFactory<LocationTag> LOCATION = registerTagFactory(new LocationTagFactory());

    public static ObjectTagFactory<?> bootstrap() {
        return PLAYER;
    }

    public static <T extends ObjectTag> void registerWithObjectFetcher(ObjectTagFactory<T> factory, String shortName, String longName) {
        Class<T> objectTag = factory.tagClass;
        ObjectTagProcessor<T> processor = factory.tagProcessor;
        ObjectType<T> newType = new ObjectType<>();
        newType.clazz = objectTag;
        if (processor != null) {
            processor.type = objectTag;
            if (factory.isReal()) {
                CoreObjectTags.generateCoreTags(processor);
            }
            newType.tagProcessor = processor;
        }
        newType.longName = longName;
        newType.shortName = shortName;
        newType.isAdjustable = Adjustable.class.isAssignableFrom(objectTag);
        factory.objectType = newType;
        ObjectFetcher.objectsByClass.put(objectTag, newType);
        String identifier;
        ObjectType.MatchesInterface matches;
        ObjectType.ValueOfInterface<T> valueOf;
        identifier = factory.objectIdentifier();
        matches = factory::cleanMatches;
        valueOf = factory::cleanValueOf;
        factory.registerTags();
        if (factory.isReal()) {
            ObjectFetcher.realObjectClassSet.add(objectTag);
            ObjectFetcher.objectsByPrefix.put(CoreUtilities.toLowerCase(identifier.trim()), newType);
            ObjectFetcher.objectsByName.put(CoreUtilities.toLowerCase(longName), newType);
            if (shortName != null) {
                ObjectFetcher.objectsByName.put(CoreUtilities.toLowerCase(shortName), newType);
            }
            newType.prefix = identifier;
            BY_OBJECT_TYPE.put(objectTag, factory);
        }
        newType.matches = matches;
        newType.valueOf = valueOf;
    }

    public static <T extends ObjectTag> void registerWithObjectFetcher(ObjectTagFactory<T> factory) {
        String longName = DebugInternals.getClassNameOpti(factory.tagClass);
        String shortName = null;
        if (longName.endsWith("Tag")) {
            shortName = longName.substring(0, longName.length() - "Tag".length());
        }
        registerWithObjectFetcher(factory, shortName, longName);
    }

    public static <T extends ObjectTag> void registerWithTagManager(ObjectTagFactory<T> factory, boolean isStatic) {
        TagManager.internalRegisterTagHandler(factory.tagClass, factory.name(), factory::handleAttribute, isStatic);
    }

    public static <T extends ObjectTag, R, F extends ObjectReferenceTagMetafactory<T, R>> F registerMetafactory(F metafactory) {
        METAFACTORIES.put(metafactory.tagClass, metafactory);
        return metafactory;
    }

    public static List<? extends ObjectReferenceTagMetafactory<?, ?>> getMetafactories(Class<? extends ObjectTag> subclass) {
        return METAFACTORIES.entrySet().stream().filter((entry) -> entry.getKey().isAssignableFrom(subclass)).map(Map.Entry::getValue).toList();
    }

    public static <T extends ObjectTag, F extends ObjectTagFactory<T>> F registerTagFactory(F factory, boolean isStatic) {
        registerWithObjectFetcher(factory);
        if (factory.name() != null) {
            registerWithTagManager(factory, isStatic);
        }
        List<? extends ObjectReferenceTagMetafactory<?, ?>> metafactories = getMetafactories(factory.tagClass);
        if (metafactories.size() > 0 && !(factory instanceof ObjectReferenceTagFactory<?, ?>)) {
            throw new ClassCastException("Factory for " + factory.tagClass.getName() + " (" + factory.getClass().getName() + ") does not extend ObjectReferenceTagFactory");
        }
        metafactories.forEach((metafactory) -> metafactory.register((ObjectReferenceTagFactory<?, ?>) factory));
        return factory;
    }

    public static <T extends ObjectTag, F extends ObjectTagFactory<T>> F registerTagFactory(F factory) {
        return registerTagFactory(factory, false);
    }

    public static <T extends ObjectTag, F extends ObjectTagFactory<T>> F registerStaticTagFactory(F factory) {
        return registerTagFactory(factory, true);
    }
}
