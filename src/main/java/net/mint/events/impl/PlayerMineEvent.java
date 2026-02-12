package net.mint.events.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.mint.events.Event;
import net.minecraft.util.math.BlockPos;

@Getter
@AllArgsConstructor
public class PlayerMineEvent extends Event {
    private final int actorId;
    private final BlockPos pos;
}