package net.mint.modules.impl.movement;

import net.mint.events.SubscribeEvent;
import net.mint.events.impl.TickEvent;
import net.mint.modules.Category;
import net.mint.modules.Feature;
import net.mint.modules.FeatureInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.LlamaEntity;

@FeatureInfo(name = "Yaw", category = Category.Movement)
public class YawFeature extends Feature {

    @SubscribeEvent
    public void onTick(TickEvent event) {
        if (getNull() || !isEnabled()) return;

        float currentYaw = mc.player.getYaw();
        float snappedYaw = Math.round(currentYaw / 45.0f) * 45.0f;

        Entity vehicle = mc.player.getVehicle();
        if (vehicle != null) {
            vehicle.setYaw(snappedYaw);
            vehicle.setHeadYaw(snappedYaw);
            if (vehicle instanceof LlamaEntity llama) {
                llama.setBodyYaw(snappedYaw);
            }
        } else {
            mc.player.setYaw(snappedYaw);
            mc.player.setHeadYaw(snappedYaw);
            mc.player.setBodyYaw(snappedYaw);
        }
    }
}