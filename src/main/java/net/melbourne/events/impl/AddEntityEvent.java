package net.melbourne.events.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.melbourne.events.Event;
import net.melbourne.modules.impl.client.NotificationsFeature;
import net.minecraft.entity.Entity;

/**
 * @see NotificationsFeature
 */
@Getter
@AllArgsConstructor
public class AddEntityEvent extends Event {
    private final Entity entity;
}