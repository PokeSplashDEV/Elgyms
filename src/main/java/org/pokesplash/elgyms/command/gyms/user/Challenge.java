package org.pokesplash.elgyms.command.gyms.user;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.badge.PlayerBadges;
import org.pokesplash.elgyms.command.CommandHandler;
import org.pokesplash.elgyms.gym.GymConfig;
import org.pokesplash.elgyms.provider.BadgeProvider;
import org.pokesplash.elgyms.provider.GymProvider;
import org.pokesplash.elgyms.util.LuckPermsUtils;
import org.pokesplash.elgyms.util.Utils;

import java.util.UUID;

public class Challenge {
	public LiteralCommandNode<ServerCommandSource> build() {
		return CommandManager.literal("challenge")
				.requires(ctx -> {
					if (ctx.isExecutedByPlayer()) {
						return LuckPermsUtils.hasPermission(ctx.getPlayer(), CommandHandler.basePermission +
								".user.challenge");
					} else {
						return true;
					}
				})
				.executes(this::usage)
				.then(CommandManager.argument("gym", StringArgumentType.string())
						.suggests((ctx, builder) -> {
							for (GymConfig gymConfig : GymProvider.getGyms().values()) {
								builder.suggest(gymConfig.getId());
							}
							return builder.buildFuture();
						})
						.executes(this::run))
				.build();
	}

	public int run(CommandContext<ServerCommandSource> context) {

		if (!context.getSource().isExecutedByPlayer()) {
			context.getSource().sendMessage(Text.literal("This command must be ran by a player."));
		}

		String gymId = StringArgumentType.getString(context, "gym");

		GymConfig gym = GymProvider.getGymById(gymId);

		if (gym == null) {
			context.getSource().sendMessage(Text.literal(Elgyms.lang.getPrefix() +
					"§cGym " + gymId + " does not exist."));
			return 1;
		}

		if (!GymProvider.getOpenGyms().contains(gym)) {
			context.getSource().sendMessage(Text.literal(Elgyms.lang.getPrefix() +
					"§cThe " + gym.getName() + " is currently closed."));
			return 1;
		}

		PlayerBadges playerBadges = BadgeProvider.getBadges(context.getSource().getPlayer());

		if (playerBadges.containsBadge(gym.getBadge().getId())) {
			context.getSource().sendMessage(Text.literal(Elgyms.lang.getPrefix() +
					"§cYou have already beaten this gym."));
			return 1;
		}

		boolean hasRequirements = true;
		for (UUID badgeId : gym.getRequirements().getRequiredBadgeIDs()) {
			if (!playerBadges.containsBadge(badgeId)) {
				hasRequirements = false;
				break;
			}
		}

		if (!hasRequirements) {
			context.getSource().sendMessage(Text.literal(Elgyms.lang.getPrefix() +
					"§cYou do not have the requirements to challenge this gym."));
			return 1;
		}

		// TODO challenge the gym.

		return 1;
	}

	public int usage(CommandContext<ServerCommandSource> context) {
		context.getSource().sendMessage(
				Text.literal(
						Elgyms.lang.getPrefix() +
						Utils.formatMessage(
						"§b§lUsage:\n§3- gym challenge <gym>", context.getSource().isExecutedByPlayer()
				))
		);

		return 1;
	}
}
