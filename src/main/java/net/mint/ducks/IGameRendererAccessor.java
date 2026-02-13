package net.mint.ducks;

import net.minecraft.client.render.Camera;

public interface IGameRendererAccessor {
    Camera mint$getCamera();
    float mint$getTickDelta();
}