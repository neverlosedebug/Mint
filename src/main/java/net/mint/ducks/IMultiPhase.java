package net.mint.ducks;

import net.minecraft.client.render.RenderLayer;

public interface IMultiPhase {
    RenderLayer.MultiPhaseParameters mint$getParameters();
}