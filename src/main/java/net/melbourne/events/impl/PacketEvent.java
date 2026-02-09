package net.melbourne.events.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.melbourne.events.Event;
import net.minecraft.network.packet.Packet;

@Getter
@AllArgsConstructor
public class PacketEvent extends Event {
    private final Packet<?> packet;

    public static class Receive extends PacketEvent {
        public Receive(Packet<?> packet) {
            super(packet);
        }
    }
}