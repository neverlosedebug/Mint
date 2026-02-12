package net.mint.events.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.mint.events.Event;

@AllArgsConstructor @Getter @Setter
public class KeyboardTickEvent extends Event {
    private float forward, sideways;
    private boolean jump, sneak;
}