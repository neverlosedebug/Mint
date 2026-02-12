package net.mint.events.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.mint.events.Event;
import net.minecraft.client.input.Input;

@AllArgsConstructor
@Getter
public class InputUpdateEvent extends Event {
    public final Input input;
}