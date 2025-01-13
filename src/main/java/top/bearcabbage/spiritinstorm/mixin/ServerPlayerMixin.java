package top.bearcabbage.spiritinstorm.mixin;

import com.mojang.authlib.GameProfile;
import eu.pb4.playerdata.api.PlayerDataApi;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
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
			lanternData.save();
		}
		exploredSpirits.clear();
		exploredSpirits.put(SpiritInStormConstant.Types.FOOD, lanternData.get(SpiritInStormConstant.Types.FOOD, Set.class) == null ? new HashSet<>() : lanternData.get(SpiritInStormConstant.Types.FOOD, Set.class));
		exploredSpirits.put(SpiritInStormConstant.Types.ADVANCEMENT, lanternData.get(SpiritInStormConstant.Types.ADVANCEMENT, Set.class) == null ? new HashSet<>() : lanternData.get(SpiritInStormConstant.Types.ADVANCEMENT, Set.class));
		exploredSpirits.put(SpiritInStormConstant.Types.BIOME, lanternData.get(SpiritInStormConstant.Types.BIOME, Set.class) == null ? new HashSet<>() : lanternData.get(SpiritInStormConstant.Types.BIOME, Set.class));
	}


	@Inject(method = "tick", at = @At("TAIL"))
	private void tick(CallbackInfo info) {
		RegistryEntry<Biome> biome = this.getWorld().getBiome(this.getBlockPos());
		if (!((SiSPlayer)this).isExplored(SpiritInStormConstant.Types.BIOME, biome.getIdAsString())) {
			SpiritInStorm.Handlers.handler(SpiritInStormConstant.Types.BIOME, this, biome.getIdAsString(), 1);
			((SiSPlayer)this).setExplored(SpiritInStormConstant.Types.BIOME, biome.getIdAsString());
		}
	}

}

