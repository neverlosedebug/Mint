package net.mint.ducks;

import net.minecraft.client.render.entity.model.EntityModel;

public interface IEntityRendererAccessor {
    EntityModel<?> mint$getModel();
}