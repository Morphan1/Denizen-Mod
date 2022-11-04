package com.morphanone.denizenmod.objects;

import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import com.morphanone.denizenmod.tags.annotations.GenerateTag;
import com.morphanone.denizenmod.tags.annotations.OptionalType;
import com.morphanone.denizenmod.tags.annotations.Tag;
import com.morphanone.denizenmod.utilities.OptionalFloat;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

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

    @GenerateTag
    public double x() {
        return position.x();
    }

    @GenerateTag
    public double y() {
        return position.y();
    }

    @GenerateTag
    public double z() {
        return position.z();
    }

    public Optional<Vec2> pitchYaw() {
        return Optional.ofNullable(pitchYaw);
    }

    @GenerateTag
    public OptionalFloat pitch() {
        return pitchYaw != null ? OptionalFloat.of(pitchYaw.x) : OptionalFloat.empty();
    }

    @GenerateTag
    public OptionalFloat yaw() {
        return pitchYaw != null ? OptionalFloat.of(pitchYaw.y) : OptionalFloat.empty();
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

    @Tag
    @OptionalType(WorldTag.class)
    public Optional<WorldTag> world() {
        return Optional.ofNullable(world);
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
            pos += "," + CoreUtilities.doubleToString(pitchYaw.x) + "," + CoreUtilities.doubleToString(pitchYaw.y);
        }
        if (world != null) {
            pos += "," + world.rawSimpleIdentity();
        }
        return pos;
    }

    @Tag
    public ElementTag simpleTag() {
        return new ElementTag(rawSimpleIdentity());
    }
}
