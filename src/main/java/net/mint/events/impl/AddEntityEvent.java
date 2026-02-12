package net.mint.events.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.mint.events.Event;
import net.mint.modules.impl.client.NotificationsFeature;
import net.minecraft.entity.Entity;

/**
 * @see NotificationsFeature
 */
@Getter
@AllArgsConstructor
public class AddEntityEvent extends Event {
    private final Entity entity;
}