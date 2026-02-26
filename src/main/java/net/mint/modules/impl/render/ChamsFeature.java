package net.mint.modules.impl.render;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.mint.modules.Category;
import net.mint.modules.Feature;
import net.mint.modules.FeatureInfo;
import net.mint.settings.types.BooleanSetting;

@FeatureInfo(name = "Chams", category = Category.Render)
public class ChamsFeature extends Feature {

    public BooleanSetting players = new BooleanSetting("Players", "Show players through blocks.", true);
    public BooleanSetting entities = new BooleanSetting("Entities", "Show living entities through blocks.", false);
    public BooleanSetting crystals = new BooleanSetting("Crystals", "Show end crystals through blocks.", true);

    public boolean shouldChams(Entity e) {
        if (!isEnabled()) return false;
        if (e == mc.player && mc.options.getPerspective().isFirstPerson()) return false;

        if (e instanceof EndCrystalEntity) return crystals.getValue();
        if (e instanceof PlayerEntity) return players.getValue();

        return entities.getValue() && (e instanceof LivingEntity);

    }
}