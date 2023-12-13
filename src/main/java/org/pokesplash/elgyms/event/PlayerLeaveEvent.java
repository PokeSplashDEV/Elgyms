package org.pokesplash.elgyms.event;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.gym.GymConfig;
import org.pokesplash.elgyms.gym.Queue;
import org.pokesplash.elgyms.provider.GymProvider;
import org.pokesplash.elgyms.util.Utils;

import java.util.ArrayList;

public class PlayerLeaveEvent implements ServerPlayConnectionEvents.Disconnect {

	@Override
	public void onPlayDisconnect(ServerPlayNetworkHandler handler, MinecraftServer server) {
		Queue queue = GymProvider.getQueueFromPlayer(handler.getPlayer().getUuid());

		if (queue != null) {
			queue.removeFromQueue(handler.getPlayer().getUuid());
		}

		// TODO gyms arent being closed.
		// Opens all of the players gyms.
		ArrayList<GymConfig> leaderGyms = GymProvider.getGymsByLeader(handler.getPlayer().getUuid());
		for (GymConfig gymConfig : leaderGyms) {
			if (!GymProvider.hasOnlineLeader(gymConfig)) {
				GymProvider.closeGym(gymConfig);
				Utils.broadcastMessage(Utils.formatPlaceholders(
						Elgyms.lang.getCloseGymMessage(), null, null, handler.getPlayer(),
						null, gymConfig
				));
			}
		}
	}
}
