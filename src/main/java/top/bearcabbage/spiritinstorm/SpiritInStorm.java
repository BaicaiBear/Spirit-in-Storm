package top.bearcabbage.spiritinstorm;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.bearcabbage.lanterninstorm.LanternInStormAPI;
import top.bearcabbage.spiritinstorm.SpiritInStormConstant.*;

public class SpiritInStorm implements ModInitializer {
	public static final String MOD_ID = "spirit-in-storm";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {}

	public static class Handlers {
		public static void handler(String type , PlayerEntity player, String value, int amount) {
			if (!player.getWorld().isClient && !((SiSPlayer)player).isExplored(type, value)) {
				if (LanternInStormAPI.addPlayerSpirit((ServerPlayerEntity) player, amount)) {
					((SiSPlayer)player).setExplored(type, value);
					((ServerPlayerEntity)player).networkHandler.sendPacket(new TitleS2CPacket(Text.literal("你获得了"+amount+"点灵魂")));
					switch (type) {
						case Types.FOOD:
							((ServerPlayerEntity)player).networkHandler.sendPacket(new SubtitleS2CPacket(Text.literal("好好吃！").withColor(0x8B4513)));
							break;
						case Types.ADVANCEMENT:
							((ServerPlayerEntity)player).networkHandler.sendPacket(new SubtitleS2CPacket(Text.literal("非！常！简！单！").withColor(0xA020F0)));
							break;
						case Types.BIOME:
							((ServerPlayerEntity)player).networkHandler.sendPacket(new SubtitleS2CPacket(Text.literal("这是哪里？").withColor(0xBFFF00)));
							break;
						case Types.EASTEREGG:
							((ServerPlayerEntity)player).networkHandler.sendPacket(new SubtitleS2CPacket(Text.literal("没有想到还有彩蛋").withColor(0xFF681F)));
							break;
					}
				}
			}
		}

	}
}