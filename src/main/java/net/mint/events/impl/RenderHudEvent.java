package net.mint.events.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.mint.events.Event;
import net.minecraft.client.gui.DrawContext;

@Getter
@AllArgsConstructor
public class RenderHudEvent extends Event {
    public DrawContext context;

}