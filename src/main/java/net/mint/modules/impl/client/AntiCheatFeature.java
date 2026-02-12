package net.mint.modules.impl.client;

import net.mint.events.SubscribeEvent;
import net.mint.events.impl.GameLoopEvent;
import net.mint.modules.Category;
import net.mint.modules.Feature;
import net.mint.modules.FeatureInfo;
import net.mint.settings.types.BooleanSetting;
import net.mint.settings.types.ModeSetting;
import net.mint.settings.types.NumberSetting;
import net.mint.utils.miscellaneous.Timer;

@FeatureInfo(name = "AntiCheat", category = Category.Client)
public class AntiCheatFeature extends Feature {
    public BooleanSetting altSwap = new BooleanSetting("AltSwap", "Uses intermediate hotbar slot to prevent swap desync on strict servers.", true);
    public BooleanSetting moveFix = new BooleanSetting("MoveFix", "Forces your player to move towards their rotation.", true);
    public ModeSetting timerMode = new ModeSetting("TimerMode", "This is to control the mode of timer we will use on different modules.", "Normal", new String[]{"Normal", "Physics"});

    public final BooleanSetting antiDesync = new BooleanSetting("AntiDesync", "Syncs inventory items to prevent desync.", false);
    public final NumberSetting desyncDelay = new NumberSetting("AntiDesyncDelay", "Delay for inventory sync.", 1, 0, 250, antiDesync.getValue());
    private final Timer timer = new Timer();

    @SubscribeEvent
    public void onLoop(GameLoopEvent event) {
        if (getNull() || !antiDesync.getValue())
            return;

        if (timer.hasTimeElapsed(desyncDelay.getValue().floatValue())) {
            mc.player.getInventory().updateItems();
            timer.reset();
        }
    }
}