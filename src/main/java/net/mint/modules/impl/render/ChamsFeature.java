package net.mint.modules.impl.render;

import net.mint.modules.Category;
import net.mint.modules.Feature;
import net.mint.modules.FeatureInfo;
import net.mint.settings.types.BooleanSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;

@FeatureInfo(name = "Chams", category = Category.Render)
public class ChamsFeature extends Feature {

    public BooleanSetting players = new BooleanSetting("Players", "Show players through blocks.", true);
    public BooleanSetting entities = new BooleanSetting("Entities", "Show living entities through blocks.", false);
    public BooleanSetting crystals = new BooleanSetting("Crystals", "Show end crystals through blocks.", true);

    public boolean shouldChams(Entity e) {
        if (!isEnabled()) return false;
        if (e == MinecraftClient.getInstance().player && MinecraftClient.getInstance().options.getPerspective().isFirstPerson()) return false;

        if (e instanceof EndCrystalEntity) return crystals.getValue();
        if (e instanceof PlayerEntity) return players.getValue();
        return entities.getValue() && (e instanceof LivingEntity);
    }

    public boolean shouldChams(EntityRenderState state) {
        if (!isEnabled()) return false;
        if (state.entityType == EntityType.PLAYER) return players.getValue();
        if (state.entityType == EntityType.END_CRYSTAL) return crystals.getValue();
        return entities.getValue() && isLiving(state.entityType);
    }

    private static boolean isLiving(EntityType<?> type) {
        return type.getSpawnGroup() != SpawnGroup.MISC;
    }
}