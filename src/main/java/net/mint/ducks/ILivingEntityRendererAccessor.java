package net.mint.ducks;

import net.minecraft.client.render.entity.state.LivingEntityRenderState;

public interface ILivingEntityRendererAccessor {
    LivingEntityRenderState mint$getCurrentState();
}