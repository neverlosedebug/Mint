package net.mint.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import net.mint.Managers;
import net.mint.Mint;
import net.mint.events.impl.RenderEntityEvent;
import net.mint.events.impl.RenderShaderEvent;
import net.mint.events.impl.RenderWorldEvent;
import net.mint.modules.impl.player.NoEntityTraceFeature;
import net.mint.modules.impl.render.AspectFeature;
import net.mint.modules.impl.render.NoRenderFeature;
import net.mint.utils.graphics.api.WorldContext;
import net.mint.utils.graphics.impl.Renderer3D;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.util.hit.EntityHitResult;
import net.mint.modules.impl.render.CustomBobbingFeature;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    @Final
    private Camera camera;

    @Shadow
    protected abstract void bobView(MatrixStack matrices, float tickDelta);

    @Shadow
    protected abstract void tiltViewWhenHurt(MatrixStack matrices, float tickDelta);


    @Inject(method = "renderWorld", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = {"ldc=hand"}))
    private void renderWorld$swap(RenderTickCounter tickCounter, CallbackInfo ci, @Local(ordinal = 2) Matrix4f matrix4f3, @Local(ordinal = 1) float tickDelta, @Local MatrixStack matrixStack) {
        if (client.player == null || client.world == null) return;

        RenderSystem.getModelViewStack().pushMatrix();

        RenderSystem.getModelViewStack().mul(matrix4f3);

        MatrixStack matrices = new MatrixStack();
        matrices.push();

        tiltViewWhenHurt(matrices, camera.getLastTickProgress());
        if (client.options.getBobView().getValue())
            bobView(matrices, camera.getLastTickProgress());

        RenderSystem.getModelViewStack().mul(matrices.peek().getPositionMatrix().invert());
        matrices.pop();

        Renderer3D.MODEL_VIEW_MATRIX.set(RenderSystem.getModelViewMatrix());
        Renderer3D.POSITION_MATRIX.set(matrixStack.peek().getPositionMatrix());

        WorldContext context = new WorldContext(matrixStack, Renderer3D.VERTEX_CONSUMERS);
        Mint.EVENT_HANDLER.post(new RenderWorldEvent(context, tickDelta));
        Renderer3D.draw(context, Renderer3D.QUADS, Renderer3D.DEBUG_LINES);

        Renderer3D.VERTEX_CONSUMERS.draw();

        Mint.EVENT_HANDLER.post(new RenderWorldEvent.Post(context, tickDelta));
        Mint.EVENT_HANDLER.post(new RenderEntityEvent.Post());

        RenderSystem.getModelViewStack().popMatrix();
    }

    @Inject(method = "renderWorld", at = @At("TAIL"))
    private void renderWorld$TAIL(RenderTickCounter renderTickCounter, CallbackInfo info) {
        Mint.EVENT_HANDLER.post(new RenderShaderEvent.Post());
    }

    /**
     * @see NoEntityTraceFeature
     */
    @ModifyExpressionValue(
            method = "findCrosshairTarget",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/projectile/ProjectileUtil;raycast(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;D)Lnet/minecraft/util/hit/EntityHitResult;"
            )
    )
    private EntityHitResult modifyEntityHitResult(EntityHitResult original) {
        var noEntityTrace = Managers.FEATURE.getFeatureFromClass(NoEntityTraceFeature.class);
        if (noEntityTrace != null && noEntityTrace.isEnabled()) {
            return null;
        }
        return original;
    }

    @Inject(method = "tiltViewWhenHurt", at = @At("HEAD"), cancellable = true)
    private void tiltViewWhenHurt(CallbackInfo info) {
        if (Managers.FEATURE.getFeatureFromClass(NoRenderFeature.class).isEnabled() && Managers.FEATURE.getFeatureFromClass(NoRenderFeature.class).hurtCamera.getValue())
            info.cancel();
    }

    /**
     * @see CustomBobbingFeature
     */
    @ModifyVariable(
            method = "bobView",
            at = @At("STORE"),
            ordinal = 1
    )
    private float modifyBobIntensity(float original) {
        var customBobbing = Managers.FEATURE.getFeatureFromClass(CustomBobbingFeature.class);
        if (customBobbing != null && customBobbing.isEnabled()) {
            return original * customBobbing.intensity.getValue().floatValue();
        }
        return original;
    }

    @Inject(method = "getBasicProjectionMatrix", at = @At("TAIL"), cancellable = true)
    public void getBasicProjectionMatrix(float fovDegrees, CallbackInfoReturnable<Matrix4f> info) {
        var aspectModule = Managers.FEATURE.getFeatureFromClass(AspectFeature.class);
        if (aspectModule != null && aspectModule.isEnabled()) {
            float aspectRatio = aspectModule.ratio.getValue().floatValue();

            Matrix4f projectionMatrix = new Matrix4f();
            projectionMatrix.setPerspective(
                    (float) Math.toRadians(fovDegrees),
                    aspectRatio,
                    0.05f,
                    this.getFarPlaneDistance()
            );

            info.setReturnValue(projectionMatrix);
        }
    }

    @Shadow
    protected abstract float getFarPlaneDistance();

    @Inject(method = "showFloatingItem", at = @At("HEAD"), cancellable = true)
    private void showFloatingItem(ItemStack floatingItem, CallbackInfo info) {
        if (Managers.FEATURE.getFeatureFromClass(NoRenderFeature.class).isEnabled() && Managers.FEATURE.getFeatureFromClass(NoRenderFeature.class).totemAnimation.getValue())
            info.cancel();
    }
}