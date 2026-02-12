package net.mint.events.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.mint.events.Event;

@Getter
@AllArgsConstructor
public class MouseEvent extends Event {
    private final int button;
}