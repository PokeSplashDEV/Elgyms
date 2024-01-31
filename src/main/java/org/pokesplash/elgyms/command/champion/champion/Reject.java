package org.pokesplash.elgyms.command.champion.champion;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.champion.ChampionConfig;
import org.pokesplash.elgyms.command.CommandHandler;
import org.pokesplash.elgyms.gym.GymConfig;
import org.pokesplash.elgyms.gym.Queue;
import org.pokesplash.elgyms.provider.GymProvider;
import org.pokesplash.elgyms.util.LuckPermsUtils;
import org.pokesplash.elgyms.util.Utils;

import java.util.UUID;

public class Reject {
	public LiteralCommandNode<ServerCommandSource> build() {
		return CommandManager.literal("reject")
				.requires(ctx -> {
					if (ctx.isExecutedByPlayer()) {
						// See's if the player executing the command is the champion.
						return GymProvider.getChampion().getChampion().getUuid().equals(ctx.getPlayer().getUuid());
					} else {
						return true;
					}
				})
				.executes(this::run)
				.build();
	}

	public int run(CommandContext<ServerCommandSource> context) {

		if (!context.getSource().isExecutedByPlayer()) {
			context.getSource().sendMessage(Text.literal("This command must be ran by a player."));
			return 1;
		}

		ChampionConfig championConfig = GymProvider.getChampion();

		if (championConfig.getChampion() == null) {
			context.getSource().sendMessage(Text.literal(Elgyms.lang.getPrefix() +
					"§cThere currently is no Champion."));
			return 1;
		}

		ServerPlayerEntity leader = context.getSource().getPlayer();

		if (!championConfig.getChampion().getUuid().equals(leader.getUuid())) {
			context.getSource().sendMessage(Text.literal(Elgyms.lang.getPrefix() +
					"§cYou are not the Champion."));
			return 1;
		}

		Queue queue = GymProvider.getChampQueue();

		if (queue.getQueue().isEmpty()) {
			context.getSource().sendMessage(Text.literal(Elgyms.lang.getPrefix() +
					"§cYou currently have no challenger for Champion."));
			return 1;
		}

		UUID challengerUuid = queue.getQueue().get(0);

		ServerPlayerEntity challenger = Elgyms.server.getPlayerManager().getPlayer(challengerUuid);

		if (challenger == null) {
			context.getSource().sendMessage(Text.literal(Elgyms.lang.getPrefix() +
					"§cThe Challenger is no longer online."));
			return 1;
		}

		if (challenger.getUuid().equals(leader.getUuid())) {
			context.getSource().sendMessage(Text.literal(Elgyms.lang.getPrefix() +
					"§cYou can not battle yourself."));
			return 1;
		}

		GymProvider.rejectChampionChallenge(challengerUuid, context.getSource().getPlayer());

		return 1;
	}

	public int usage(CommandContext<ServerCommandSource> context) {
		context.getSource().sendMessage(
				Text.literal(
						Elgyms.lang.getPrefix() +
						Utils.formatMessage(
						"§b§lUsage:\n§3- champ reject", context.getSource().isExecutedByPlayer()
				))
		);

		return 1;
	}
}
