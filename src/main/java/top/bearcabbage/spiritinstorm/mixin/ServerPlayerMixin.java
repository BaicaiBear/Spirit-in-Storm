package top.bearcabbage.spiritinstorm.mixin;

import com.mojang.authlib.GameProfile;
import eu.pb4.playerdata.api.PlayerDataApi;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.bearcabbage.lanterninstorm.LanternInStorm;
import top.bearcabbage.lanterninstorm.player.Player;
import top.bearcabbage.spiritinstorm.SiSPlayer;
import top.bearcabbage.spiritinstorm.SpiritInStormConstant.*;

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
		lanternData.set(Types.FOOD, exploredSpirits.get(Types.FOOD));
		lanternData.save();
	}

	@Inject(method = "<init>", at = @At("RETURN"))
	private void init(CallbackInfo info) {
		Player.Config lanternData = new Player.Config(PlayerDataApi.getPathFor((ServerPlayerEntity)((PlayerEntity)this)).resolve("explored_spirits.json"));
		if (lanternData == null) {
			LanternInStorm.LOGGER.warn("No Explored Spirit Data, generating blank format...");
			lanternData.set(Types.FOOD, new HashSet<>());
			lanternData.save();
		}
		exploredSpirits.clear();
		exploredSpirits.put(Types.FOOD, lanternData.get(Types.FOOD, Set.class) == null ? new HashSet<>() : lanternData.get(Types.FOOD, Set.class));
	}


}

