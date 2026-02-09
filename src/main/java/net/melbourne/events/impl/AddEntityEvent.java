package net.melbourne.events.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.melbourne.events.Event;
import net.minecraft.entity.Entity;

// cyka @SEE notifs
@Getter
@AllArgsConstructor
public class AddEntityEvent extends Event {
    private final Entity entity;
}