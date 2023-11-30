package org.pokesplash.elgyms.util;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.minecraft.server.network.ServerPlayerEntity;
import org.pokesplash.elgyms.Elgyms;

public abstract class LuckPermsUtils {
	/**
	 * Checks a user has a given permission.
	 * @param user The user to check the permission on.
	 * @param permission The permission to check the user has.
	 * @return true if the user has the permission.
	 */
	public static boolean hasPermission(ServerPlayerEntity user, String permission) {
		User playerLP = LuckPermsProvider.get().getUserManager().getUser(user.getUuid());

		if (playerLP == null) {
			Elgyms.LOGGER.error("Could not find player " + user + " in LuckPerms.");
			return false;
		}

		return playerLP.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
	}
}
