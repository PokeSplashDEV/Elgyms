package org.pokesplash.elgyms.command.champion.champion;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.pokemon.Pokemon;
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
import org.pokesplash.elgyms.exception.GymException;
import org.pokesplash.elgyms.gym.GymConfig;
import org.pokesplash.elgyms.gym.Queue;
import org.pokesplash.elgyms.provider.BattleProvider;
import org.pokesplash.elgyms.provider.GymProvider;
import org.pokesplash.elgyms.util.LuckPermsUtils;
import org.pokesplash.elgyms.util.Utils;
import org.pokesplash.teampreview.TeamPreview;

import java.util.ArrayList;
import java.util.UUID;

public class Accept {
	public LiteralCommandNode<ServerCommandSource> build() {
		return CommandManager.literal("accept")
				.requires(ctx -> {
					if (ctx.isExecutedByPlayer()) {
						// See's if the player executing the command is the champion.
						return Elgyms.championConfig.getChampion().getUuid().equals(ctx.getPlayer().getUuid());
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

		// If its team preview, open the preview window, else just start the battle.
		if (championConfig.getRequirements().isTeamPreview()) {
			try {
				ArrayList<Pokemon> leaderTeam = BattleProvider.getChampTeam();
				TeamPreview.createPreview(leader.getUuid(), challenger.getUuid(),
						leaderTeam, BattleProvider.toList(Cobblemon.INSTANCE.getStorage().getParty(challenger)),
						e -> {
					try {
						BattleProvider.beginChampionBattle(challenger, leader);
					} catch (Exception ex) {
						// Sends error to leader. Tells challenger something went wrong.
						leader.sendMessage(Text.literal("§c" + ex.getMessage()));
						challenger.sendMessage(Text.literal("§c" + "Something went wrong, the leader has more info."));

                        Elgyms.LOGGER.error(ex.getMessage());
                    }
				});
				TeamPreview.openPreview(leader.getUuid());
				TeamPreview.openPreview(challenger.getUuid());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			BattleProvider.beginChampionBattle(challenger, leader);
		}

		return 1;
	}

	public int usage(CommandContext<ServerCommandSource> context) {
		context.getSource().sendMessage(
				Text.literal(
						Elgyms.lang.getPrefix() +
						Utils.formatMessage(
						"§b§lUsage:\n§3- champ accept", context.getSource().isExecutedByPlayer()
				))
		);

		return 1;
	}
}
