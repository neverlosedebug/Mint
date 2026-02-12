package net.mint.services.impl;

import net.mint.Mint;
import net.mint.services.Service;
import net.mint.events.SubscribeEvent;
import net.mint.events.impl.PacketReceiveEvent;
import net.mint.events.impl.TickEvent;
import net.mint.mixins.accessors.WorldRendererAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.BlockBreakingInfo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.BlockBreakingProgressS2CPacket;
import net.minecraft.util.math.BlockPos;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static net.mint.utils.Globals.mc;

public class BreakService extends Service {

    private final Map<BlockPos, Float> breakingProgressMap = new HashMap<>();

    private static final double MINING_PLAYER_RANGE_SQ = 36.0;

    public BreakService() {
        super("Break", "Provides information about actively broken blocks.");
        Mint.EVENT_HANDLER.subscribe(this);
    }

    @SubscribeEvent
    public void onPacketReceive(PacketReceiveEvent event) {
        if (getNull() || mc.worldRenderer == null) return;

        if (event.getPacket() instanceof BlockBreakingProgressS2CPacket packet) {
            BlockPos pos = packet.getPos();
            float rawProgress = packet.getProgress() / 8.0f;

            if (rawProgress < 0.01f || rawProgress > 1.0f) {
                breakingProgressMap.remove(pos);
            } else {
                breakingProgressMap.put(pos, rawProgress);
            }
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent event) {
        if (getNull() || mc.worldRenderer == null) return;
        
        Map<BlockPos, Float> currentBreaking = getActiveBreakingProgress();
        breakingProgressMap.keySet().retainAll(currentBreaking.keySet());
        breakingProgressMap.putAll(currentBreaking);
    }

    public Map<Integer, BlockBreakingInfo> getActiveBreakingInfos() {
        if (mc.worldRenderer == null) return Collections.emptyMap();
        WorldRendererAccessor accessor = (WorldRendererAccessor) mc.worldRenderer;
        return accessor.getBlockBreakingInfos();
    }

    public Map<BlockPos, Float> getActiveBreakingProgress() {
        Map<Integer, BlockBreakingInfo> infos = getActiveBreakingInfos();
        if (infos.isEmpty()) return Collections.emptyMap();

        Map<BlockPos, Float> progressMap = new HashMap<>();

        for (BlockBreakingInfo info : infos.values()) {
            BlockPos pos = info.getPos();
            Entity entity = mc.world.getEntityById(info.getActorId());

            if (!(entity instanceof PlayerEntity player)) continue;

            if (player.getPos().squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > MINING_PLAYER_RANGE_SQ) {
                continue;
            }

            float progress = info.getStage() / 8.0f;
            progressMap.put(pos, progress);
        }

        return progressMap;
    }

    public boolean isBlockBeingMined(BlockPos pos) {
        return getActiveBreakingInfos().values().stream()
                .anyMatch(info -> info.getPos().equals(pos));
    }

    public Float getBlockMiningProgress(BlockPos pos) {
        return breakingProgressMap.getOrDefault(pos, 0.0f);
    }

    public BlockBreakingInfo getBreakingInfoForPos(BlockPos pos) {
        for (BlockBreakingInfo info : getActiveBreakingInfos().values()) {
            if (info.getPos().equals(pos)) {
                return info;
            }
        }
        return null;
    }

    public boolean getNull() { return mc.world == null || mc.player == null; }
}