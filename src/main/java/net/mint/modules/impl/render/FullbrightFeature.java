package net.mint.modules.impl.render;

import net.mint.events.SubscribeEvent;
import net.mint.events.impl.PlayerUpdateEvent;
import net.mint.ducks.ISimpleOption;
import net.mint.modules.Category;
import net.mint.modules.Feature;
import net.mint.modules.FeatureInfo;
import net.mint.settings.types.ModeSetting;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

@FeatureInfo(name = "Fullbright", category = Category.Render)
public class FullbrightFeature extends Feature {
    public ModeSetting mode = new ModeSetting("Mode", "The method to light up the game.", "Gamma", new String[]{"Gamma", "Potion"});

    @SubscribeEvent
    public void onPlayerUpdate(PlayerUpdateEvent event) {
        if (mode.getValue().equalsIgnoreCase("Potion")) {
            if (!mc.player.hasStatusEffect(StatusEffects.NIGHT_VISION))
                mc.player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, StatusEffectInstance.INFINITE));
        } else {
            if (mc.player.hasStatusEffect(StatusEffects.NIGHT_VISION))
                mc.player.removeStatusEffect(StatusEffects.NIGHT_VISION);

            ((ISimpleOption) (Object) mc.options.getGamma()).mint$setValue(10000.0);
        }
    }

    @Override
    public void onEnable() {
        if (getNull())
            return;

        if (!mode.getValue().equalsIgnoreCase("Potion"))
            return;

        if (!mc.player.hasStatusEffect(StatusEffects.NIGHT_VISION))
            mc.player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, StatusEffectInstance.INFINITE));
    }

    @Override
    public void onDisable() {
        if (getNull())
            return;

        if (!mode.getValue().equalsIgnoreCase("Potion"))
            return;

        if (mc.player.hasStatusEffect(StatusEffects.NIGHT_VISION))
            mc.player.removeStatusEffect(StatusEffects.NIGHT_VISION);

        if (mc.options != null)
            ((ISimpleOption) (Object) mc.options.getGamma()).mint$setValue(1.0);
    }

    @Override
    public String getInfo() {
        return mode.getValue();
    }
}
