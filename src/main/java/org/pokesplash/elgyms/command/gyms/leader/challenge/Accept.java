package org.pokesplash.elgyms.command.gyms.leader.challenge;

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
import org.pokesplash.elgyms.command.CommandHandler;
import org.pokesplash.elgyms.exception.GymException;
import org.pokesplash.elgyms.gym.GymConfig;
import org.pokesplash.elgyms.gym.Queue;
import org.pokesplash.elgyms.provider.BattleProvider;
import org.pokesplash.elgyms.provider.GymProvider;
import org.pokesplash.elgyms.util.ElgymsUtils;
import org.pokesplash.elgyms.util.LuckPermsUtils;
import org.pokesplash.elgyms.util.Utils;
import org.pokesplash.teampreview.TeamPreview;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Accept {
	public LiteralCommandNode<ServerCommandSource> build() {
		return CommandManager.literal("accept")
				.requires(ctx -> {
					if (ctx.isExecutedByPlayer()) {
						return LuckPermsUtils.hasPermission(ctx.getPlayer(), CommandHandler.basePermission +
								".leader.gym.accept");
					} else {
						return true;
					}
				})
				.executes(this::usage)
				.then(CommandManager.argument("gym", StringArgumentType.string())
						.suggests((ctx, builder) -> {
							for (GymConfig gymConfig : GymProvider.getGyms().values()) {
								if (gymConfig.containsLeader(ctx.getSource().getPlayer().getUuid())) {
									builder.suggest(gymConfig.getId());
								}
							}
							return builder.buildFuture();
						})
						.executes(this::run))
				.build();
	}

	public int run(CommandContext<ServerCommandSource> context) {

		try {
			if (!context.getSource().isExecutedByPlayer()) {
				context.getSource().sendMessage(Text.literal("This command must be ran by a player."));
				return 1;
			}

			String gymId = StringArgumentType.getString(context, "gym");

			GymConfig gym = GymProvider.getGymById(gymId);

			if (gym == null) {
				context.getSource().sendMessage(Text.literal(Elgyms.lang.getPrefix() +
						"§cGym " + gymId + "§c does not exist."));
				return 1;
			}

			if (!gym.containsLeader(context.getSource().getPlayer().getUuid())) {
				context.getSource().sendMessage(Text.literal(Elgyms.lang.getPrefix() +
						"§cYou are not a leader of this gym."));
				return 1;
			}

			Queue queue = GymProvider.getQueueFromGym(gym);

			if (queue.getQueue().isEmpty()) {
				context.getSource().sendMessage(Text.literal(Elgyms.lang.getPrefix() +
						"§cThis gym has no challengers."));
				return 1;
			}

			UUID challengerUuid = queue.getQueue().get(0);

			ServerPlayerEntity challenger = Elgyms.server.getPlayerManager().getPlayer(challengerUuid);

			if (challenger == null) {
				context.getSource().sendMessage(Text.literal(Elgyms.lang.getPrefix() +
						"§cChallenger is no longer online."));
				return 1;
			}

			ServerPlayerEntity leader = context.getSource().getPlayer();

			if (challenger.getUuid().equals(leader.getUuid())) {
				context.getSource().sendMessage(Text.literal(Elgyms.lang.getPrefix() +
						"§cYou can not battle yourself."));
				return 1;
			}

			// If its team preview, open the preview window, else just start the battle.
			if (gym.getRequirements().isTeamPreview()) {
				try {
					ArrayList<Pokemon> leaderTeam = BattleProvider.getLeaderTeam(leader, gym);
					TeamPreview.createPreview(leader.getUuid(), challenger.getUuid(),
							leaderTeam, BattleProvider.toList(Cobblemon.INSTANCE.getStorage().getParty(challenger)),
							e -> {
								try {
									// Moves the Pokemon at the lead index to the beginning of the list.
									List<Pokemon> leaderPokemon = e.getPlayer1().getPokemon();
									Pokemon leaderLead = leaderPokemon.get(e.getPlayer1().getLead());
									leaderPokemon.remove(e.getPlayer1().getLead());
									leaderPokemon.add(0, leaderLead);

									// Moves the Pokemon at the lead index to the beginning of the list.
									List<Pokemon> challengerPokemon = e.getPlayer2().getPokemon();
									Pokemon challengerLead = challengerPokemon.get(e.getPlayer2().getLead());
									challengerPokemon.remove(e.getPlayer2().getLead());
									challengerPokemon.add(0, challengerLead);

									BattleProvider.beginBattle(challenger, leader, gym,
											ElgymsUtils.toJson(leaderPokemon), ElgymsUtils.toJson(challengerPokemon));
								} catch (Exception ex) {
									// Sends error to leader. Tells challenger something went wrong.
									leader.sendMessage(Text.literal("§c" + ex.getMessage()));
									challenger.sendMessage(Text.literal("§c" + "Something went wrong, the leader has more info."));

									if (!(ex instanceof GymException)) {
										Elgyms.LOGGER.error(ex.getMessage());
									}
								}
							});
					TeamPreview.openPreview(leader.getUuid());
					TeamPreview.openPreview(challenger.getUuid());
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				BattleProvider.beginBattle(challenger, leader, gym);
			}
		}
		catch (Exception e) {
			context.getSource().sendMessage(Text.literal("§cSomething went wrong."));
			Elgyms.LOGGER.error(e.getStackTrace());
		}

		return 1;
	}

	public int usage(CommandContext<ServerCommandSource> context) {
		context.getSource().sendMessage(
				Text.literal(
						Elgyms.lang.getPrefix() +
						Utils.formatMessage(
						"§b§lUsage:\n§3- gym accept <gym>", context.getSource().isExecutedByPlayer()
				))
		);

		return 1;
	}
}
