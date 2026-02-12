package net.mint.modules.impl.render;

import net.mint.events.SubscribeEvent;
import net.mint.events.impl.PlayerDeathEvent;
import net.mint.modules.Category;
import net.mint.modules.Feature;
import net.mint.modules.FeatureInfo;
import net.mint.settings.types.ColorSetting;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;

import java.awt.*;

@FeatureInfo(name = "KillEffects", category = Category.Render)
public class KillEffectsFeature extends Feature {
    public ColorSetting color = new ColorSetting("Color", "The color of the lightning bolt.", new Color(219, 127, 255));

    @SubscribeEvent
    public void onPlayerDeath(PlayerDeathEvent event) {
        LightningEntity entity = new LightningEntity(EntityType.LIGHTNING_BOLT, mc.world);

        entity.setPosition(event.getPlayer().getPos());
        entity.setId(-701);

        mc.world.addEntity(entity);
    }
}
