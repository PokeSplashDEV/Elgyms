package org.pokesplash.elgyms.event;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.pokesplash.elgyms.provider.BadgeProvider;
import org.pokesplash.elgyms.provider.GymProvider;

public class PlayerJoinEvent implements ServerPlayConnectionEvents.Join {
	@Override
	public void onPlayReady(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
		// TODO check name change on teams and badges
		GymProvider.updateName(handler.getPlayer().getUuid(), handler.getPlayer().getDisplayName().getString());

		BadgeProvider.getBadges(handler.getPlayer());

		BadgeProvider.updateName(handler.getPlayer().getUuid(), handler.getPlayer().getDisplayName().getString());


		// TODO If no badges, create instance.
	}
}
