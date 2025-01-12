package top.bearcabbage.spiritinstorm.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.bearcabbage.spiritinstorm.SpiritInStorm;
import vectorwing.farmersdelight.common.block.PieBlock;

import java.util.function.Supplier;

@Mixin(PieBlock.class)
public abstract class FarmersDelightPieBlockMixin extends Block {
    public FarmersDelightPieBlockMixin(Settings settings, Supplier<Item> pieSlice) {
        super(settings);
    }

    @Shadow public abstract ItemStack getPieSliceItem();

    @Inject(method = "consumeBite", at = @At(value = "RETURN", ordinal = 1))
    protected void consumeBite(World level, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfoReturnable<ActionResult> cir) {
        SpiritInStorm.Handlers.food(player, this.getPieSliceItem().getItem());
    }

}
