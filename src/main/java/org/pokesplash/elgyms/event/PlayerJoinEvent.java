package org.pokesplash.elgyms.event;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.gym.GymConfig;
import org.pokesplash.elgyms.provider.BadgeProvider;
import org.pokesplash.elgyms.provider.GymProvider;
import org.pokesplash.elgyms.util.Utils;

import java.util.ArrayList;

public class PlayerJoinEvent implements ServerPlayConnectionEvents.Join {
	@Override
	public void onPlayReady(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
		BadgeProvider.getBadges(handler.getPlayer());

		BadgeProvider.updateName(handler.getPlayer().getUuid(), handler.getPlayer().getName().getString());

		// Opens all the players gyms.
		GymProvider.openAllGyms(handler.getPlayer());
	}
}
