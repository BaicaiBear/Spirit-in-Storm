package top.bearcabbage.spiritinstorm;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
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
		public static void handler(String type , PlayerEntity player, String value, int amount) {
			if (!player.getWorld().isClient && !((SiSPlayer)player).isExplored(type, value)) {
				if (LanternInStormAPI.addPlayerSpirit((ServerPlayerEntity) player, amount)) {
					((SiSPlayer)player).setExplored(type, value);
					((ServerPlayerEntity)player).networkHandler.sendPacket(new ParticleS2CPacket(ParticleTypes.ELDER_GUARDIAN, false, player.getX(), player.getY(), player.getZ(), 0.0F, 0.0F, 0.0F, 0.0F, 1));
					((ServerPlayerEntity)player).networkHandler.sendPacket(new SubtitleS2CPacket(Text.of("你获得了"+amount+"点灵魂")));
				}
			}
		}

	}
}