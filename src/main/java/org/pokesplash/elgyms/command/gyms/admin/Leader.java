package org.pokesplash.elgyms.command.gyms.admin;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.apache.logging.log4j.core.jmx.Server;
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.badge.PlayerBadges;
import org.pokesplash.elgyms.command.CommandHandler;
import org.pokesplash.elgyms.config.CategoryConfig;
import org.pokesplash.elgyms.gym.GymConfig;
import org.pokesplash.elgyms.provider.BadgeProvider;
import org.pokesplash.elgyms.provider.GymProvider;
import org.pokesplash.elgyms.util.LuckPermsUtils;
import org.pokesplash.elgyms.util.Utils;

import java.util.stream.Stream;

public class Leader {
	public LiteralCommandNode<ServerCommandSource> build() {
		return CommandManager.literal("leader")
				.requires(ctx -> {
					if (ctx.isExecutedByPlayer()) {
						return LuckPermsUtils.hasPermission(ctx.getPlayer(), CommandHandler.basePermission +
								".admin.leader");
					} else {
						return true;
					}
				})
				.executes(this::usage)
				.then(CommandManager.literal("add")
						.executes(this::usage)
						.then(CommandManager.argument("gym", StringArgumentType.string())
						.suggests((ctx, builder) -> {
							for (GymConfig gym : GymProvider.getGyms().values()) {
								builder.suggest(gym.getId());
							}
							return builder.buildFuture();
						})
								.executes(this::usage)
								.then(CommandManager.argument("player", StringArgumentType.string())
										.suggests((ctx, builder) -> {
											for (PlayerBadges badges : BadgeProvider.getBadges().values()) {
												builder.suggest(badges.getName());
											}

											return builder.buildFuture();
										})
										.executes(this::add))))


				.build();
	}

	public int add(CommandContext<ServerCommandSource> context) {

		String gymString = StringArgumentType.getString(context, "gym");

		GymConfig gym = GymProvider.getGymById(GymConfig.nameToId(gymString));

		if (gym == null) {
			context.getSource().sendMessage(Text.literal(Utils.formatMessage(
					"§cGym " + gymString + " could not be found.", context.getSource().isExecutedByPlayer()
			)));
			return 1;
		}

		String playerString = StringArgumentType.getString(context, "player");
		PlayerBadges player = BadgeProvider.getBadges(playerString);

		if (player == null) {
			context.getSource().sendMessage(Text.literal(Utils.formatMessage(
					"§cPlayer " + playerString + " could not be found.", context.getSource().isExecutedByPlayer()
			)));
			return 1;
		}

		if (gym.containsLeader(player.getUuid())) {
			context.getSource().sendMessage(Text.literal(Utils.formatMessage(
					"§c" + player.getName() + " is already a leader of this gym.",
					context.getSource().isExecutedByPlayer()
			)));
			return 1;
		}

		gym.addLeader(new org.pokesplash.elgyms.gym.Leader(player.getUuid(), player.getName()));
		gym.write();

		context.getSource().sendMessage(Text.literal(Utils.formatMessage(
				"§aAdded " + player.getName() + " to " + gym.getName(), context.getSource().isExecutedByPlayer()
		)));

		return 1;
	}

	public int usage(CommandContext<ServerCommandSource> context) {
		context.getSource().sendMessage(
				Text.literal(
						Elgyms.lang.getPrefix() +
								Utils.formatMessage(
										"§b§lUsage:\n§3- gym create <category> <gym>",
										context.getSource().isExecutedByPlayer()
								))
		);
		return 1;
	}
}
