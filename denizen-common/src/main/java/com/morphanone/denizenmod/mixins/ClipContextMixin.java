package com.morphanone.denizenmod.mixins;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClipContext.class)
public abstract class ClipContextMixin {
    /**
     * <pre>
     * Normal usage of ClipContext's constructor requires a non-null entity due to its direct
     * call to {@link CollisionContext#of(Entity)}. This is undesirable for its use in {@link com.morphanone.denizenmod.utilities.RayTrace},
     * where it is possible no entity is involved.
     *
     * It is unlikely other mods will perform anything other than the same modification to this particular line.
     *
     * Last verified: 1.19.2
     * </pre>
     */
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/shapes/CollisionContext;of(Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/world/phys/shapes/CollisionContext;"))
    private CollisionContext denizen_collisionContextOf(Entity entity) {
        return entity != null ? CollisionContext.of(entity) : CollisionContext.empty();
    }
}
