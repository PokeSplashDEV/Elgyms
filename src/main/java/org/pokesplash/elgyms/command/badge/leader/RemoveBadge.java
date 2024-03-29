package org.pokesplash.elgyms.command.badge.leader;

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
import org.pokesplash.elgyms.config.E4Team;
import org.pokesplash.elgyms.config.Reward;
import org.pokesplash.elgyms.gym.GymConfig;
import org.pokesplash.elgyms.gym.GymRewards;
import org.pokesplash.elgyms.provider.BadgeProvider;
import org.pokesplash.elgyms.provider.E4Provider;
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

		try {
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
						"§cGym " + gymId + "§c does not exist."));
				return 1;
			}

			if (sender != null && !gym.containsLeader(sender.getUuid()) &&
					!LuckPermsUtils.hasPermission(sender, CommandHandler.basePermission +
							".admin.badges")) {
				context.getSource().sendMessage(Text.literal(Elgyms.lang.getPrefix() +
						"§cYou are not a leader of this gym."));
				return 1;
			}

			if (!badges.containsBadge(gym.getBadge().getId())) {
				context.getSource().sendMessage(Text.literal(Elgyms.lang.getPrefix() +
						"§c" + badges.getName() + "§c doesn't have this badge."));
				return 1;
			}

			CategoryConfig categoryConfig = Elgyms.config.getCategoryByName(gym.getCategoryName());

			if (categoryConfig == null) {
				context.getSource().sendMessage(Text.literal(Elgyms.lang.getPrefix() +
						"§c" + gym.getName() + "§c doesn't have a valid category."));
				return 1;
			}

			try {
				badges.removeBadge(categoryConfig, gym.getBadge());
			} catch (Exception e) {
				e.printStackTrace();
			}



			// If the player has no more E4 badges, remove their E4 team.
			if (gym.isE4() && !badges.hasE4Badges()) {
				E4Provider.deleteTeam(E4Provider.getTeam(badges.getUuid()));
			}

			context.getSource().sendMessage(Text.literal(Elgyms.lang.getPrefix() +
					"§2Removed " + gym.getBadge().getName() + "§2 from " + badges.getName() + "."));
		}
		catch (Exception e) {
			context.getSource().sendMessage(Text.literal("§cSomething went wrong."));
			e.printStackTrace();
		}

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
