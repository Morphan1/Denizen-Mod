package com.morphanone.denizenmod.utilities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Predicate;

public class RayTrace {
    public static Result entities(Level level, Vec3 start, Vec3 direction, double distance, double size, Predicate<Entity> filter) {
        if (filter == null) {
            filter = EntitySelector.NO_SPECTATORS;
        }
        Vec3 scaledDirection = direction.normalize().scale(distance);
        Vec3 end = start.add(scaledDirection);
        List<Entity> entities = level.getEntities((Entity) null, new AABB(start, start).expandTowards(scaledDirection).inflate(size), filter);
        Entity closestEntity = null;
        double closestDistanceSq = Double.MAX_VALUE;
        for (Entity entity : entities) {
            Vec3 pos = entity.getBoundingBox().clip(start, end).orElse(null);
            if (pos != null) {
                double distanceSq = pos.distanceToSqr(start);
                if (distanceSq < closestDistanceSq) {
                    closestEntity = entity;
                    closestDistanceSq = distanceSq;
                }
            }
        }
        return new Result(closestEntity);
    }

    public static class Result {
        public Entity entity;
        public BlockPos block;

        public Result(Entity entity) {
            this.entity = entity;
        }

        public Result(BlockPos block) {
            this.block = block;
        }
    }
}
