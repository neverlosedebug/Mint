package net.melbourne.modules.impl.movement;

import net.melbourne.events.SubscribeEvent;
import net.melbourne.events.impl.PlayerTickEvent;
import net.melbourne.modules.Category;
import net.melbourne.modules.Feature;
import net.melbourne.modules.FeatureInfo;
import net.melbourne.settings.types.NumberSetting;

@FeatureInfo(name = "Flight", category = Category.Movement)
public class FlightFeature extends Feature {

    public NumberSetting horizontalSpeed = new NumberSetting("Horizontal Speed", "Horizontal flight speed.", 1.0, 0.1, 10.0);
    public NumberSetting verticalSpeed = new NumberSetting("Vertical Speed", "Vertical flight speed.", 1.0, 0.1, 5.0);

    @Override
    public void onEnable() {
        if (getNull()) return;
        mc.player.getAbilities().flying = false;
        mc.player.getAbilities().allowFlying = false;
    }

    @Override
    public void onDisable() {
        if (getNull()) return;
        mc.player.setVelocity(0, mc.player.getVelocity().y, 0);
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event) {
        if (getNull()) return;

        double hSpeed = horizontalSpeed.getValue().doubleValue();
        double vSpeed = verticalSpeed.getValue().doubleValue();

        var input = mc.player.input;
        var movement = input.getMovementInput();
        float strafe = movement.x;
        float forward = movement.y;

        float yaw = mc.player.getYaw();

        double motionX = 0.0;
        double motionZ = 0.0;

        if (forward != 0.0f || strafe != 0.0f) {
            double direction = Math.toRadians(yaw + 90.0f);
            double rx = Math.cos(direction);
            double rz = Math.sin(direction);

            motionX = (forward * hSpeed * rx) + (strafe * hSpeed * rz);
            motionZ = (forward * hSpeed * rz) - (strafe * hSpeed * rx);
        }

        double motionY = 0.0;
        if (mc.options.jumpKey.isPressed()) {
            motionY = vSpeed;
        } else if (mc.options.sneakKey.isPressed()) {
            motionY = -vSpeed;
        }

        mc.player.setVelocity(motionX, motionY, motionZ);
        mc.player.fallDistance = 0.0f;
        mc.player.setOnGround(false);
    }
}