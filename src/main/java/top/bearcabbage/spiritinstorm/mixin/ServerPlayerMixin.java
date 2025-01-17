package top.bearcabbage.spiritinstorm.mixin;

import com.mojang.authlib.GameProfile;
import eu.pb4.playerdata.api.PlayerDataApi;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.bearcabbage.lanterninstorm.LanternInStorm;
import top.bearcabbage.lanterninstorm.player.Player;
import top.bearcabbage.spiritinstorm.SiSPlayer;
import top.bearcabbage.spiritinstorm.SpiritInStorm;
import top.bearcabbage.spiritinstorm.SpiritInStormConstant;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerMixin extends PlayerEntity implements SiSPlayer {
	@Shadow public abstract ServerWorld getServerWorld();

	@Unique private final Map<String, Set<String>> exploredSpirits = new java.util.HashMap<>();

	public ServerPlayerMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
		super(world, pos, yaw, gameProfile);
	}

	@Unique public boolean isExplored(String type, String key) {
		return exploredSpirits.get(type).contains(key);
	}

	@Unique public void setExplored(String type, String key) {
		if (!exploredSpirits.containsKey(type)) exploredSpirits.put(type, new java.util.HashSet<>());
		exploredSpirits.get(type).add(key);
		Player.Config lanternData = new Player.Config(PlayerDataApi.getPathFor((ServerPlayerEntity)((PlayerEntity)this)).resolve("explored_spirits.json"));
		lanternData.set(type, exploredSpirits.get(type));
		lanternData.save();
	}

	@Inject(method = "<init>", at = @At("RETURN"))
	private void init(CallbackInfo info) {
		Player.Config lanternData = new Player.Config(PlayerDataApi.getPathFor((ServerPlayerEntity)((PlayerEntity)this)).resolve("explored_spirits.json"));
		if (lanternData == null) {
			LanternInStorm.LOGGER.warn("No Explored Spirit Data, generating blank format...");
			lanternData.set(SpiritInStormConstant.Types.FOOD, new HashSet<>());
			lanternData.set(SpiritInStormConstant.Types.ADVANCEMENT, new HashSet<>());
			lanternData.set(SpiritInStormConstant.Types.BIOME, new HashSet<>());
			lanternData.set(SpiritInStormConstant.Types.EASTEREGG, new HashSet<>());
			lanternData.save();
		}
		exploredSpirits.clear();
		exploredSpirits.put(SpiritInStormConstant.Types.FOOD, lanternData.get(SpiritInStormConstant.Types.FOOD, Set.class) == null ? new HashSet<>() : lanternData.get(SpiritInStormConstant.Types.FOOD, Set.class));
		exploredSpirits.put(SpiritInStormConstant.Types.ADVANCEMENT, lanternData.get(SpiritInStormConstant.Types.ADVANCEMENT, Set.class) == null ? new HashSet<>() : lanternData.get(SpiritInStormConstant.Types.ADVANCEMENT, Set.class));
		exploredSpirits.put(SpiritInStormConstant.Types.BIOME, lanternData.get(SpiritInStormConstant.Types.BIOME, Set.class) == null ? new HashSet<>() : lanternData.get(SpiritInStormConstant.Types.BIOME, Set.class));
		exploredSpirits.put(SpiritInStormConstant.Types.EASTEREGG, lanternData.get(SpiritInStormConstant.Types.EASTEREGG, Set.class) == null ? new HashSet<>() : lanternData.get(SpiritInStormConstant.Types.EASTEREGG, Set.class));
	}

	public ItemStack eatFood(World world, ItemStack stack, FoodComponent foodComponent) {
		SpiritInStorm.Handlers.handler(SpiritInStormConstant.Types.FOOD, this, stack.getItem().toString(), 1);
		return super.eatFood(world, stack, foodComponent);
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void tick(CallbackInfo info) {
		if(!getServerWorld().getRegistryKey().getValue().equals(Identifier.of("mirrortree","bedroom"))) SpiritInStorm.Handlers.handler(SpiritInStormConstant.Types.BIOME, this, this.getWorld().getBiome(this.getBlockPos()).getIdAsString(), 1);
	}

}

