package net.melbourne.modules.impl.movement;

import net.melbourne.events.SubscribeEvent;
import net.melbourne.events.impl.TickEvent;
import net.melbourne.modules.Category;
import net.melbourne.modules.Feature;
import net.melbourne.modules.FeatureInfo;
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