package net.melbourne.mixins;

import net.melbourne.Managers;
import net.melbourne.modules.impl.render.ChamsFeature;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.entity.state.EntityRenderState;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {

    private static boolean melbourne$chamsPass = false;

    @Inject(
            method = "render(Lnet/minecraft/client/render/entity/state/EntityRenderState;DDDLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void melbourne$renderChams(
            EntityRenderState state,
            double x, double y, double z,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            CallbackInfo ci
    ) {
        if (melbourne$chamsPass) return;

        ChamsFeature chams = Managers.FEATURE.getFeatureFromClass(ChamsFeature.class);
        if (chams == null || !chams.shouldChams(state)) {
            return;
        }

        melbourne$chamsPass = true;

        int prevDepthFunc = GL11.glGetInteger(GL11.GL_DEPTH_FUNC);
        boolean wasDepthMask = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthFunc(GL11.GL_GREATER);
        GL11.glDepthMask(false);

        ((EntityRenderDispatcher)(Object)this).render(state, x, y, z, matrices, vertexConsumers, light);

        if (vertexConsumers instanceof VertexConsumerProvider.Immediate immediate) {
            immediate.draw();
        }

        GL11.glDepthFunc(prevDepthFunc);
        GL11.glDepthMask(wasDepthMask);
        melbourne$chamsPass = false;
    }
}