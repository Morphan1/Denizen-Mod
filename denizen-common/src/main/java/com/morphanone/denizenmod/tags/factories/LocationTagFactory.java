package com.morphanone.denizenmod.tags.factories;

import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import com.morphanone.denizenmod.objects.LocationTag;
import com.morphanone.denizenmod.objects.WorldTag;
import com.morphanone.denizenmod.tags.TagFactories;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class LocationTagFactory extends ObjectTagFactory<LocationTag> {
    public LocationTagFactory() {
        super(LocationTag.class);
    }

    @Override
    public String name() {
        return "location";
    }

    @Override
    public String objectIdentifier() {
        return "l";
    }

    @Override
    public LocationTag getDefault(TagContext context) {
        return null;
    }

    public WorldTag parseWorld(String input, TagContext context) {
        String worldName = TagFactories.WORLD.cleanInput(input);
        WorldTag world = TagFactories.WORLD.valueOf(worldName, context);
        if (world == null) {
            world = WorldTag.fromName(worldName);
        }
        return world;
    }

    public LocationTag fromIdentity(String input, TagContext context) {
        List<String> split = CoreUtilities.split(input, ',');
        if (split.size() < 2) {
            return null;
        }
        double x;
        double y;
        try {
            x = Double.parseDouble(split.get(0));
            y = Double.parseDouble(split.get(1));
        }
        catch (Exception e) {
            return null;
        }
        // x,y
        if (split.size() == 2) {
            return new LocationTag(new Vec3(x, y, 0.0));
        }
        double z;
        WorldTag world;
        try {
            z = Double.parseDouble(split.get(2));
            // x,y,z
            if (split.size() == 3) {
                return new LocationTag(new Vec3(x, y, z));
            }
        }
        catch (Exception e) {
            // x,y,world
            if (split.size() == 3) {
                world = parseWorld(split.get(2), context);
                return new LocationTag(new Vec3(x, y, 0.0), world);
            }
            return null;
        }
        // x,y,z,world
        if (split.size() == 4) {
            world = parseWorld(split.get(3), context);
            return new LocationTag(new Vec3(x, y, z), world);
        }
        Vec2 pitchYaw;
        try {
            pitchYaw = new Vec2(Float.parseFloat(split.get(3)), Float.parseFloat(split.get(4)));
        }
        catch (Exception e) {
            return null;
        }
        // x,y,z,pitch,yaw
        if (split.size() == 5) {
            return new LocationTag(new Vec3(x, y, z), pitchYaw);
        }
        // x,y,z,pitch,yaw,world
        if (split.size() == 6) {
            world = parseWorld(split.get(5), context);
            return new LocationTag(new Vec3(x, y, z), pitchYaw, world);
        }
        return null;
    }

    @Override
    public LocationTag valueOf(String input, TagContext context) {
        return fromIdentity(input, context);
    }

    @Override
    public boolean matches(String input) {
        return fromIdentity(input, CoreUtilities.noDebugContext) != null;
    }
}
