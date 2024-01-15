package org.pokesplash.elgyms.command.gyms.admin;

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
import org.pokesplash.elgyms.gym.GymConfig;
import org.pokesplash.elgyms.gym.Positions;
import org.pokesplash.elgyms.provider.BadgeProvider;
import org.pokesplash.elgyms.provider.GymProvider;
import org.pokesplash.elgyms.util.LuckPermsUtils;
import org.pokesplash.elgyms.util.Utils;

public class Position {
	public LiteralCommandNode<ServerCommandSource> build() {
		return CommandManager.literal("position")
				.requires(ctx -> {
					if (ctx.isExecutedByPlayer()) {
						return LuckPermsUtils.hasPermission(ctx.getPlayer(), CommandHandler.basePermission +
								".admin.position");
					} else {
						return true;
					}
				})
				.executes(this::usage)
				.then(CommandManager.argument("gym", StringArgumentType.string())
						.suggests((ctx, builder) -> {
							for (GymConfig gym : GymProvider.getGyms().values()) {
								builder.suggest(gym.getId());
							}
							return builder.buildFuture();
						})
						.executes(this::usage)
						.then(CommandManager.literal("leader")
								.executes(this::leader))
						.then(CommandManager.literal("challenger")
								.executes(this::challenger))
						.then(CommandManager.literal("spectator")
								.executes(this::spectator))
				)
				.build();
	}

	public int leader(CommandContext<ServerCommandSource> context) {

		if (!context.getSource().isExecutedByPlayer()) {
			context.getSource().sendMessage(Text.literal("This command must be ran by a player."));
		}

		ServerPlayerEntity player = context.getSource().getPlayer();

		String gymString = StringArgumentType.getString(context, "gym");

		GymConfig gym = GymProvider.getGymById(GymConfig.nameToId(gymString));

		if (gym == null) {
			context.getSource().sendMessage(Text.literal(Utils.formatMessage(
					"§cGym " + gymString + " could not be found.", context.getSource().isExecutedByPlayer()
			)));
			return 1;
		}

		Positions positions = gym.getPositions();

		positions.getLeader().setPitch(player.getPitch());
		positions.getLeader().setYaw(player.getYaw());
		positions.getLeader().setX(player.getX());
		positions.getLeader().setY(player.getY());
		positions.getLeader().setZ(player.getZ());
		positions.getLeader().setWorld(player.getWorld().getRegistryKey().getValue());

		gym.setPositions(positions);

		context.getSource().sendMessage(Text.literal("§2Set leader to position for " + gym.getName()));

		return 1;
	}

	public int challenger(CommandContext<ServerCommandSource> context) {

		if (!context.getSource().isExecutedByPlayer()) {
			context.getSource().sendMessage(Text.literal("This command must be ran by a player."));
		}

		ServerPlayerEntity player = context.getSource().getPlayer();

		String gymString = StringArgumentType.getString(context, "gym");

		GymConfig gym = GymProvider.getGymById(GymConfig.nameToId(gymString));

		if (gym == null) {
			context.getSource().sendMessage(Text.literal(Utils.formatMessage(
					"§cGym " + gymString + " could not be found.", context.getSource().isExecutedByPlayer()
			)));
			return 1;
		}

		Positions positions = gym.getPositions();

		positions.getChallenger().setPitch(player.getPitch());
		positions.getChallenger().setYaw(player.getYaw());
		positions.getChallenger().setX(player.getX());
		positions.getChallenger().setY(player.getY());
		positions.getChallenger().setZ(player.getZ());
		positions.getChallenger().setWorld(player.getWorld().getRegistryKey().getValue());

		gym.setPositions(positions);

		context.getSource().sendMessage(Text.literal("§2Set challenger to position for " + gym.getName()));

		return 1;
	}

	public int spectator(CommandContext<ServerCommandSource> context) {

		if (!context.getSource().isExecutedByPlayer()) {
			context.getSource().sendMessage(Text.literal("This command must be ran by a player."));
		}

		ServerPlayerEntity player = context.getSource().getPlayer();

		String gymString = StringArgumentType.getString(context, "gym");

		GymConfig gym = GymProvider.getGymById(GymConfig.nameToId(gymString));

		if (gym == null) {
			context.getSource().sendMessage(Text.literal(Utils.formatMessage(
					"§cGym " + gymString + " could not be found.", context.getSource().isExecutedByPlayer()
			)));
			return 1;
		}

		Positions positions = gym.getPositions();

		positions.getSpectator().setPitch(player.getPitch());
		positions.getSpectator().setYaw(player.getYaw());
		positions.getSpectator().setX(player.getX());
		positions.getSpectator().setY(player.getY());
		positions.getSpectator().setZ(player.getZ());
		positions.getSpectator().setWorld(player.getWorld().getRegistryKey().getValue());

		gym.setPositions(positions);

		context.getSource().sendMessage(Text.literal("§2Set spectator to position for " + gym.getName()));

		return 1;
	}


	public int usage(CommandContext<ServerCommandSource> context) {
		context.getSource().sendMessage(
				Text.literal(
						Elgyms.lang.getPrefix() +
								Utils.formatMessage(
										"§b§lUsage:\n§3- gym position <gym> [leader|challenger|spectator]",
										context.getSource().isExecutedByPlayer()
								))
		);
		return 1;
	}
}
