package net.mint.modules.impl.player;

import net.mint.Managers;
import net.mint.services.Services;
import net.mint.events.SubscribeEvent;
import net.mint.events.impl.TickEvent;
import net.mint.modules.Category;
import net.mint.modules.Feature;
import net.mint.modules.FeatureInfo;
import net.mint.modules.impl.client.AntiCheatFeature;
import net.mint.settings.types.BooleanSetting;
import net.mint.settings.types.NumberSetting;
import net.mint.utils.miscellaneous.math.MathUtils;
import net.mint.utils.entity.player.PlayerUtils;

@FeatureInfo(name = "Timer", category = Category.Player)
public class TimerFeature extends Feature {
    public NumberSetting factor = new NumberSetting("Factor", "The multiplier that will be added to the game's speed.", 1.0f, 0.0f, 20.0f);
    public BooleanSetting whileEating = new BooleanSetting("WhileEating", "Allows you to use timer while eating.", true);
    private boolean physics = true;

    @Override
    public void onEnable() {
        if (getNull())
            return;
        physics = true;
        applyTimer();
    }

    @Override
    public void onDisable() {
        if (getNull())
            return;
        Services.WORLD.setTimerMultiplier(1.0f);
        physics = true;
    }

    @SubscribeEvent(priority = Integer.MIN_VALUE)
    public void onTick(TickEvent event) {
        if (mc.player == null || mc.world == null)
            return;

        if (PlayerUtils.isEating() && !whileEating.getValue()) {
            Services.WORLD.setTimerMultiplier(1.0f);
            return;
        }

        applyTimer();
    }

    private void applyTimer() {
        AntiCheatFeature antiCheat = (AntiCheatFeature) Managers.FEATURE.getFeatureByName("AntiCheat");
        String mode = antiCheat != null && antiCheat.isEnabled()
                ? antiCheat.timerMode.getValue()
                : "Normal";

        switch (mode) {
            case "Normal":
                Services.WORLD.setTimerMultiplier(factor.getValue().floatValue());
                break;
            case "Physics":
                if (physics) {
                    physics = false;
                    for (int i = 0; i < factor.getValue().intValue(); i++) {
                        mc.player.tick();
                    }
                }
                break;
            default:
                Services.WORLD.setTimerMultiplier(factor.getValue().floatValue());
                break;
        }
    }

    @Override
    public String getInfo() {
        return MathUtils.round(factor.getValue().floatValue(), 1) + "";
    }
}