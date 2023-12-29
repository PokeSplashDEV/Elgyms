package org.pokesplash.elgyms.command.gyms.leader.badge;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.badge.PlayerBadges;
import org.pokesplash.elgyms.command.CommandHandler;
import org.pokesplash.elgyms.config.CategoryConfig;
import org.pokesplash.elgyms.gym.GymConfig;
import org.pokesplash.elgyms.provider.BadgeProvider;
import org.pokesplash.elgyms.provider.GymProvider;
import org.pokesplash.elgyms.util.LuckPermsUtils;
import org.pokesplash.elgyms.util.Utils;

public class RemoveBadge {
	public LiteralCommandNode<ServerCommandSource> build() {
		return CommandManager.literal("remove")
				.requires(ctx -> {
					if (ctx.isExecutedByPlayer()) {
						return LuckPermsUtils.hasPermission(ctx.getPlayer(), CommandHandler.basePermission +
								".leader.badges.remove");
					} else {
						return true;
					}
				})
				.executes(this::usage)
				.then(CommandManager.argument("player", StringArgumentType.string())
						.suggests((ctx, builder) -> {
							for (PlayerBadges badges : BadgeProvider.getBadges().values()) {
								builder.suggest(badges.getName());
							}
							return builder.buildFuture();
						})
						.executes(this::run)
						.then(CommandManager.argument("gym", StringArgumentType.string())
								.suggests((ctx, builder) -> {
									for (GymConfig gymConfig : GymProvider.getGyms().values()) {
										if (gymConfig.containsLeader(ctx.getSource().getPlayer().getUuid()) ||
												LuckPermsUtils.hasPermission(ctx.getSource().getPlayer(),
														CommandHandler.basePermission +
																".admin.badges")) {
											builder.suggest(gymConfig.getId());
										}
									}
									return builder.buildFuture();
								})
								.executes(this::run)))
				.build();
	}

	public int run(CommandContext<ServerCommandSource> context) {

		if (!context.getSource().isExecutedByPlayer()) {
			context.getSource().sendMessage(Text.literal("This command must be ran by a player."));
		}

		ServerPlayerEntity sender = context.getSource().getPlayer();

		String playerName = StringArgumentType.getString(context, "player");

		PlayerBadges badges = BadgeProvider.getBadges(playerName);

		if (badges == null) {
			context.getSource().sendMessage(Text.literal(Elgyms.lang.getPrefix() +
					"§cCould not find badges for player " + playerName));
			return 1;
		}

		String gymId = StringArgumentType.getString(context, "gym");

		GymConfig gym = GymProvider.getGymById(gymId);

		if (gym == null) {
			context.getSource().sendMessage(Text.literal(Elgyms.lang.getPrefix() +
					"§cGym " + gymId + " does not exist."));
			return 1;
		}

		if (!gym.containsLeader(sender.getUuid()) &&
				!LuckPermsUtils.hasPermission(sender, CommandHandler.basePermission +
						".admin.badges")) {
			context.getSource().sendMessage(Text.literal(Elgyms.lang.getPrefix() +
					"§cYou are not a leader of this gym."));
			return 1;
		}

		if (!badges.containsBadge(gym.getBadge().getId())) {
			context.getSource().sendMessage(Text.literal(Elgyms.lang.getPrefix() +
					"§c" + badges.getName() + " doesn't have this badge."));
			return 1;
		}

		CategoryConfig categoryConfig = Elgyms.config.getCategoryByName(gym.getCategoryName());

		if (categoryConfig == null) {
			context.getSource().sendMessage(Text.literal(Elgyms.lang.getPrefix() +
					"§c" + gym.getName() + " doesn't have a valid category."));
			return 1;
		}

		badges.removeBadge(categoryConfig, gym.getBadge());

		context.getSource().sendMessage(Text.literal(Elgyms.lang.getPrefix() +
				"§2Removed " + gym.getBadge().getName() + " badge from " + badges.getName() + "."));

		return 1;
	}

	public int usage(CommandContext<ServerCommandSource> context) {
		context.getSource().sendMessage(
				Text.literal(
						Elgyms.lang.getPrefix() +
						Utils.formatMessage(
						"§b§lUsage:\n§3- badges remove <player> <gym>", context.getSource().isExecutedByPlayer()
				))
		);

		return 1;
	}
}
