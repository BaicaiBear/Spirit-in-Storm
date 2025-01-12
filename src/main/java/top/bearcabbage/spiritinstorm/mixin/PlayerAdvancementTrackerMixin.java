package top.bearcabbage.spiritinstorm.mixin;

import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.bearcabbage.spiritinstorm.SpiritInStorm;
import top.bearcabbage.spiritinstorm.SpiritInStormConstant;

@Mixin(PlayerAdvancementTracker.class)
public class PlayerAdvancementTrackerMixin {

    @Shadow private ServerPlayerEntity owner;

    @Inject(method = "grantCriterion", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancement/AdvancementRewards;apply(Lnet/minecraft/server/network/ServerPlayerEntity;)V"))
    private void onGrantCriterion(AdvancementEntry advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        if (advancement.id().getNamespace().equals("minecraft")
                && advancement.id().getPath().contains("story/") || advancement.id().getPath().contains("nether/") || advancement.id().getPath().contains("end/") || advancement.id().getPath().contains("adventure/") || advancement.id().getPath().contains("husbandry/")) {
            if (advancement.value().display().get().getFrame().equals(AdvancementFrame.TASK)) {
                SpiritInStorm.Handlers.handler(SpiritInStormConstant.Types.ADVANCEMENT, this.owner, advancement.toString(), 1);
            } else if (advancement.value().display().get().getFrame().equals(AdvancementFrame.GOAL)) {
                SpiritInStorm.Handlers.handler(SpiritInStormConstant.Types.ADVANCEMENT, this.owner, advancement.toString(), 2);
            } else if (advancement.value().display().get().getFrame().equals(AdvancementFrame.CHALLENGE)) {
                SpiritInStorm.Handlers.handler(SpiritInStormConstant.Types.ADVANCEMENT, this.owner, advancement.toString(), 3);
            }
        }
    }
}
