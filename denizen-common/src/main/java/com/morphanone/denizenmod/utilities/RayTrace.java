package com.morphanone.denizenmod.utilities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
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
        Vec3 closestPosition = null;
        double closestDistanceSq = Double.MAX_VALUE;
        for (Entity entity : entities) {
            Vec3 pos = entity.getBoundingBox().clip(start, end).orElse(null);
            if (pos != null) {
                double distanceSq = pos.distanceToSqr(start);
                if (distanceSq < closestDistanceSq) {
                    closestEntity = entity;
                    closestPosition = pos;
                    closestDistanceSq = distanceSq;
                }
            }
        }
        return new Result(closestEntity, closestPosition);
    }

    public static Result blocks(Level level, Vec3 start, Vec3 direction, double distance, boolean ignoreNonColliding, ClipContext.Fluid fluidCollision) {
        Vec3 scaledDirection = direction.normalize().scale(distance);
        Vec3 end = start.add(scaledDirection);
        @SuppressWarnings("ConstantConditions") // NonNull final parameter patched out with ClipContextMixin
        BlockHitResult nmsResult = level.clip(new ClipContext(start, end, ignoreNonColliding ? ClipContext.Block.COLLIDER : ClipContext.Block.OUTLINE, fluidCollision, null));
        return new Result(nmsResult.getBlockPos(), nmsResult.getLocation());
    }

    public static Result any(Level level, Vec3 start, Vec3 direction, double distance, double size, boolean ignoreNonColliding, ClipContext.Fluid fluidCollision, Predicate<Entity> filter) {
        Result blockResult = blocks(level, start, direction, distance, ignoreNonColliding, fluidCollision);
        double blockDistanceSq = distance * distance;
        if (blockResult.block != null) {
            blockDistanceSq = blockResult.position.distanceToSqr(start);
        }
        Result entityResult = entities(level, start, direction, distance, size, filter);
        if (blockResult.block == null) {
            return entityResult;
        }
        else if (entityResult.entity == null) {
            return blockResult;
        }
        else {
            double entityDistanceSq = entityResult.position.distanceToSqr(start);
            return entityDistanceSq < blockDistanceSq ? entityResult : blockResult;
        }
    }

    public static class Result {
        public Entity entity;

        public BlockPos block;

        public Vec3 position;

        public Result(Entity entity, Vec3 position) {
            this.entity = entity;
            this.position = position;
        }

        public Result(BlockPos block, Vec3 position) {
            this.block = block;
            this.position = position;
        }
    }
}
