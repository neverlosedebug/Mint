package net.mint.ducks;

import net.minecraft.client.model.ModelPart;

public interface IEntityModelAccessor {
    ModelPart mint$getPart(String name); // example
    void mint$setVisible(boolean visible);
}