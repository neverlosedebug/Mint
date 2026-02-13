package net.mint.ducks;

import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;

public interface IClientPlayNetworkHandlerDuck {
    void mint$onGameStateChange(GameStateChangeS2CPacket packet);
}