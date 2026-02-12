package net.mint.mixins;

import net.mint.Mint;
import net.mint.events.impl.PlayerMineEvent;
import net.minecraft.entity.player.BlockBreakingInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBreakingInfo.class)
public class BlockBreakingInfoMixin {
    @Inject(method = "compareTo(Lnet/minecraft/entity/player/BlockBreakingInfo;)I", at = @At("HEAD"))
    private void compareTo(BlockBreakingInfo other, CallbackInfoReturnable<Integer> cir) {
        BlockBreakingInfo info = (BlockBreakingInfo)(Object)this;
        
        Mint.EVENT_HANDLER.post(new PlayerMineEvent(info.getActorId(), info.getPos()));
    }
}