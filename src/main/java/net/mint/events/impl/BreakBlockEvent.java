package net.mint.events.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.mint.events.Event;
import net.minecraft.util.math.BlockPos;

@Getter
@AllArgsConstructor
public class BreakBlockEvent extends Event {
    public BlockPos pos;
}