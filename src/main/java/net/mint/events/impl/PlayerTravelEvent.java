package net.mint.events.impl;

import lombok.Getter;
import lombok.Setter;
import net.mint.events.Event;
import net.minecraft.util.math.Vec3d;

@Getter
@Setter
public class PlayerTravelEvent extends Event {
    private Vec3d movementInput;

    public PlayerTravelEvent(Vec3d movementInput) {
        this.movementInput = movementInput;
    }
}