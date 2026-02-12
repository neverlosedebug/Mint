package net.mint.events.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.mint.events.Event;

@AllArgsConstructor
@Getter
@Setter
public class ChatSendEvent extends Event {
    private final String message;
    private boolean cancelled = false;
}