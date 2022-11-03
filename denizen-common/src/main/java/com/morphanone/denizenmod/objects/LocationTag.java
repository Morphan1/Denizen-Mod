package com.morphanone.denizenmod.objects;

import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import com.morphanone.denizenmod.tags.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class LocationTag extends AbstractObjectTag {
    public Vec3 position;

    public Vec2 pitchYaw;

    public WorldTag world;

    public LocationTag(Vec3 position) {
        this(position, null, null);
    }

    public LocationTag(Vec3 position, Vec2 pitchYaw) {
        this(position, pitchYaw, null);
    }

    public LocationTag(Vec3 position, WorldTag world) {
        this(position, null, world);
    }

    public LocationTag(Vec3 position, Vec2 pitchYaw, WorldTag world) {
        this.position = position;
        this.pitchYaw = pitchYaw;
        this.world = world;
    }

    public double x() {
        return position.x();
    }

    public double y() {
        return position.y();
    }

    public double z() {
        return position.z();
    }

    public float pitch() {
        return pitchYaw.x;
    }

    public float yaw() {
        return pitchYaw.y;
    }

    public int blockX() {
        return Mth.floor(x());
    }

    public int blockY() {
        return Mth.floor(y());
    }

    public int blockZ() {
        return Mth.floor(z());
    }

    @Override
    public String rawSimpleIdentity() {
        String blockPos = blockX() + "," + blockY() + "," + blockZ();
        if (world != null) {
            blockPos += "," + world.rawSimpleIdentity();
        }
        return blockPos;
    }

    @Override
    public String rawIdentity() {
        String pos = CoreUtilities.doubleToString(x()) + "," + CoreUtilities.doubleToString(y()) + "," + CoreUtilities.doubleToString(z());
        if (pitchYaw != null) {
            pos += "," + CoreUtilities.doubleToString(pitch()) + "," + CoreUtilities.doubleToString(yaw());
        }
        if (world != null) {
            pos += "," + world.rawSimpleIdentity();
        }
        return pos;
    }

    @Tag("simple")
    public ElementTag simpleTag() {
        return new ElementTag(rawSimpleIdentity());
    }
}
