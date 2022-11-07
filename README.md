# For Users
***There's currently no reason to use this project outside of simple curiosity. The most useful available command is Narrate.***

The main focus for this project is currently server-side Fabric Denizen scripting.
That said, it also offers an experimental client-side implementation with currently no special/extra features.

Eventual compatibility with most Denizen-Bukkit scripts is a secondary goal, but is not guaranteed.

# For Developers
Documentation is rather lacking at the moment. Feel free to ping `@Morphan1` in the Denizen Discord with any questions.

Denizen-Mod is laid out similarly to other Denizen projects, with a few key differences.
By far the largest is due to a side effect of the manner this project loads separate Denizen-Core implementations (for server and client).

**Extra care must be taken to ensure no Core classes are loaded outside of their sandboxes.**

Most of the time, this won't be a problem when working in the Common module of this project, but for example in any other module 
you cannot call `Debug` methods directly. Furthermore, to register implementation-specific (or otherwise external) tags
you must go through the proper bridge channels.

For more information on the technical workings, take a look at [DenizenMod.java](denizen-common/src/main/java/com/morphanone/denizenmod/DenizenMod.java).

Another significant change (if you're familiar with developing Denizen ObjectTags) is that static `valueOf` and `matches` methods are less desirable than
implementations of `ObjectTagFactory` registered through [TagFactories](denizen-common/src/main/java/com/morphanone/denizenmod/tags/TagFactories.java).

Beyond that, DenizenMod also provides an easy auto-registration system for tags in the form of methods on the ObjectTag itself. See
[EntityTag](denizen-common/src/main/java/com/morphanone/denizenmod/objects/EntityTag.java) for an example. Note that it supports both
returning specific ObjectTag types, as well as auto-generating ElementTags from primitive types (as well as OptionalLong/Double/etc
and parsing `Optional<ObjectTag>` with `OptionalType` annotations.

Names can be placed inside `Tag("name")` or `GenerateTag("name")`. Otherwise they will be assumed from the method name, stripping "Tag" from the end.

This creates a tag called `target` that returns an EntityTag:
```java
    @Tag
    public EntityTag targetTag() {
        return value().map((entity) -> entity instanceof Mob mob ? TagFactories.ENTITY_ANY.of(mob) : null).orElse(null);
    }
```

This creates a tag called `world` that returns a WorldTag:
```java
    @Tag
    @OptionalType(WorldTag.class)
    public Optional<WorldTag> world() {
        return value().map(Entity::getLevel).map(TagFactories.WORLD::of);
    }
```

This creates a tag called `name` that generates an ElementTag from a String:
```java
    @GenerateTag("name")
    public String getRawNameString() {
        return getNameString().orElse(null);
    }
```
