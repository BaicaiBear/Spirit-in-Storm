package top.bearcabbage.spiritinstorm.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.bearcabbage.spiritinstorm.SiSPlayer;
import top.bearcabbage.spiritinstorm.SpiritInStorm;
import top.bearcabbage.spiritinstorm.SpiritInStormConstant;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {

    public AnvilScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    @Inject(method = "onTakeOutput", at = @At("TAIL"))
    private void onTakeOutput(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        if ((stack.isOf(Items.CARROT) || stack.isOf(Items.GOLDEN_CARROT)) && stack.getName().getLiteralString().contains("AC is watching you") && player instanceof ServerPlayerEntity serverPlayer && ((SiSPlayer)serverPlayer).isExplored(SpiritInStormConstant.Types.EASTEREGG, "AC is watching you")) {
            SpiritInStorm.Handlers.handler(SpiritInStormConstant.Types.EASTEREGG, serverPlayer, "AC is watching you", 1);
            ((SiSPlayer)serverPlayer).setExplored(SpiritInStormConstant.Types.EASTEREGG, "AC is watching you");
        }
    }

}
