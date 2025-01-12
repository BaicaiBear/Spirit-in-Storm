package top.bearcabbage.spiritinstorm;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.listener.GameEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.bearcabbage.lanterninstorm.LanternInStormAPI;
import top.bearcabbage.spiritinstorm.SpiritInStormConstant.*;

public class SpiritInStorm implements ModInitializer {
	public static final String MOD_ID = "spirit-in-storm";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		LOGGER.info("Hello Fabric world!");
	}

	public static class Handlers {
		public static void food(PlayerEntity player, Item item) {
			if (!player.getWorld().isClient && !((SiSPlayer)player).isExplored(Types.FOOD, item.toString())) {
				if (LanternInStormAPI.addPlayerSpirit((ServerPlayerEntity) player, 1)) {
					((SiSPlayer)player).setExplored(Types.FOOD, item.toString());
					((ServerPlayerEntity)player).networkHandler.sendPacket(new ParticleS2CPacket(ParticleTypes.ELDER_GUARDIAN, false, player.getX(), player.getY(), player.getZ(), 0.0F, 0.0F, 0.0F, 0.0F, 1));
					player.sendMessage(Text.of("You have gained 1 spirit!"));
				}
			}
		}
	}
}