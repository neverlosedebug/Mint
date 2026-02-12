package net.mint.events.impl;

import net.mint.events.Event;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Hand;
import lombok.Getter;

@Getter
public class ItemRenderEvent extends Event {
    private final Hand hand;
    private final MatrixStack matrixStack;

    public ItemRenderEvent(Hand hand, MatrixStack matrixStack) {
        this.hand = hand;
        this.matrixStack = matrixStack;
    }
}