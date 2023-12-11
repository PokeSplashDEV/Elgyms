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
		// TODO test name change on badges
		BadgeProvider.getBadges(handler.getPlayer());

		BadgeProvider.updateName(handler.getPlayer().getUuid(), handler.getPlayer().getDisplayName().getString());

		// Opens all the players gyms.
		ArrayList<GymConfig> leaderGyms = GymProvider.getGymsByLeader(handler.getPlayer().getUuid());
		leaderGyms.removeAll(GymProvider.getOpenGyms());
		for (GymConfig gymConfig : leaderGyms) {
			GymProvider.openGym(gymConfig);
			Utils.broadcastMessage(Utils.formatPlaceholders(
					Elgyms.lang.getOpenGymMessage(), null, null, handler.getPlayer(),
					null, gymConfig
			));
		}
	}
}
