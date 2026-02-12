package net.mint.services.impl;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Getter;
import net.mint.Mint;
import net.mint.mixins.accessors.*;
import net.mint.services.Service;
import net.mint.utils.Globals;
import net.minecraft.client.gl.*;
import net.minecraft.client.render.*;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.lwjgl.system.MemoryStack;

import java.awt.*;
import java.util.Map;
import java.util.OptionalInt;
import java.util.function.Consumer;
import java.util.function.Function;

@Getter
public class ShaderService extends Service implements Globals {
    private final OutlineVertexConsumerProvider vertexConsumers = new OutlineVertexConsumerProvider(VertexConsumerProvider.immediate(new BufferAllocator(256)));
    private final Framebuffer framebuffer = new SimpleFramebuffer(Mint.MOD_ID + "_outline_framebuffer", mc.getWindow().getFramebufferWidth(), mc.getWindow().getFramebufferHeight(), true);

    public ShaderService() {
        super("Shader", "Shader chams");
    }

    private final Function<RenderPhase.TextureBase, RenderLayer> OUTLINE_SHADER_LAYER = Util.memoize(texture -> RenderLayer.of(Mint.MOD_ID + "_shader_layer", 1536, RenderPipelines.OUTLINE_NO_CULL, RenderLayer.MultiPhaseParameters.builder()
            .texture(texture)
            .target(new RenderPhase.Target(Mint.MOD_ID + "_outline_shader_target", () -> framebuffer))
            .build(RenderLayer.OutlineMode.IS_OUTLINE)));

    public VertexConsumerProvider create(VertexConsumerProvider parent, Color color) {
        return layer -> {
            VertexConsumer parentBuffer = parent.getBuffer(layer);

            if (!(layer instanceof RenderLayer.MultiPhase) || ((RenderLayerMultiPhaseParametersAccessor) (Object) ((RenderLayerMultiPhaseAccessor) layer).getPhases()).getOutlineMode() == RenderLayer.OutlineMode.NONE) {
                return parentBuffer;
            }

            vertexConsumers.setColor(color.getRed(), color.getGreen(), color.getBlue(), 255);

            VertexConsumer outlineBuffer = vertexConsumers.getBuffer(OUTLINE_SHADER_LAYER.apply(((RenderLayerMultiPhaseParametersAccessor) (Object) ((RenderLayerMultiPhaseAccessor) layer).getPhases()).getTexture()));
            return outlineBuffer != null ? VertexConsumers.union(outlineBuffer, parentBuffer) : parentBuffer;
        };
    }

    public void prepare() {
        RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> Mint.MOD_ID + "_outline", framebuffer.getColorAttachmentView(), OptionalInt.of(0)).close();
    }

    public void render(String name, int stackSize, Consumer<Std140Builder> consumer) {
        PostEffectProcessor shader = mc.getShaderLoader().loadPostEffect(Identifier.of(Mint.MOD_ID, name.toLowerCase()), DefaultFramebufferSet.MAIN_ONLY);

            for (PostEffectPass pass : ((PostEffectProcessorAccessor) shader).getPasses()) {
            Map<String, GpuBuffer> uniforms = ((PostEffectPassAccessor) pass).getUniformBuffers();
            try (MemoryStack memoryStack = MemoryStack.stackPush()) {
                Std140Builder std140Builder = Std140Builder.onStack(memoryStack, stackSize);
                consumer.accept(std140Builder);

                uniforms.put(name + "Config", RenderSystem.getDevice().createBuffer(() -> Mint.NAME + "-" + name + "Config", 128, std140Builder.get()));
            }
        }

        shader.render(framebuffer, ((GameRendererAccessor) mc.gameRenderer).getPool());

        framebuffer.drawBlit(mc.getFramebuffer().getColorAttachmentView());
    }
}