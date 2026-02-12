package net.mint.events.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.mint.events.Event;
import net.minecraft.entity.player.PlayerEntity;

@Getter
@AllArgsConstructor
public class PlayerDeathEvent extends Event {
    private final PlayerEntity player;
}