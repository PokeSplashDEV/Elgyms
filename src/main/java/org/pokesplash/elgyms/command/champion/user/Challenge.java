package org.pokesplash.elgyms.command.champion.user;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.badge.PlayerBadges;
import org.pokesplash.elgyms.champion.ChampionConfig;
import org.pokesplash.elgyms.command.CommandHandler;
import org.pokesplash.elgyms.gym.GymConfig;
import org.pokesplash.elgyms.gym.Leader;
import org.pokesplash.elgyms.provider.BadgeProvider;
import org.pokesplash.elgyms.provider.BattleProvider;
import org.pokesplash.elgyms.provider.E4Provider;
import org.pokesplash.elgyms.provider.GymProvider;
import org.pokesplash.elgyms.util.ElgymsUtils;
import org.pokesplash.elgyms.util.LuckPermsUtils;
import org.pokesplash.elgyms.util.Utils;

import java.util.ArrayList;
import java.util.UUID;

public class Challenge {
	public LiteralCommandNode<ServerCommandSource> build() {
		return CommandManager.literal("challenge")
				.requires(ctx -> {
					if (ctx.isExecutedByPlayer()) {
						return LuckPermsUtils.hasPermission(ctx.getPlayer(), CommandHandler.basePermission +
								".user.champion.challenge");
					} else {
						return true;
					}
				})
				.executes(this::run)
				.build();
	}

	public int run(CommandContext<ServerCommandSource> context) {

		try {
			if (!context.getSource().isExecutedByPlayer()) {
				context.getSource().sendMessage(Text.literal("This command must be ran by a player."));
				return 1;
			}

			ServerPlayerEntity challenger = context.getSource().getPlayer();

			PlayerBadges playerBadges = BadgeProvider.getBadges(challenger);

			ChampionConfig championConfig = GymProvider.getChampion();

			// If the player doesn't have the required badge, tell them.
			if (!playerBadges.containsBadge(championConfig.getRequiredBadge())) {
				GymConfig requiredGym = GymProvider.getGymFromBadge(championConfig.getRequiredBadge());

				String output = requiredGym != null ?
						"§cYou need to have " + requiredGym.getBadge().getName() + " §cto challenge the champion." :
						"§cYou do not have the correct requirements to challenge the champion.";

				context.getSource().sendMessage(Text.literal(Elgyms.lang.getPrefix() + output));
				return 1;
			}

			Leader champion = championConfig.getChampion();

			// If there is no champion, set the challenger to champion.
			if (champion == null) {
				championConfig.setChampion(new Leader(challenger.getUuid()));
				championConfig.runWinnerRewards(challenger.getName().getString());
				return 1;
			}

			// Makes sure the champion isn't challenging themselves.
			if (challenger.getUuid().equals(challenger.getUuid())) {
				context.getSource().sendMessage(Text.literal(Elgyms.lang.getPrefix() +
						"§cYou are already Champion."
				));
				return 1;
			}

			PlayerBadges championBadges = BadgeProvider.getBadges(champion.getUuid());

			// If the champion isn't online, don't let them challenge.
			if (Elgyms.server.getPlayerManager().getPlayer(champion.getUuid()) == null) {
				String output = championBadges != null ?
						"§c" +  championBadges.getName() + " isn't currently online." :
						"§cThe champion isn't currently online.";

				context.getSource().sendMessage(Text.literal(Elgyms.lang.getPrefix() + output
				));
				return 1;
			}

			PlayerPartyStore party = Cobblemon.INSTANCE.getStorage().getParty(context.getSource().getPlayer());

			ArrayList<Pokemon> pokemons = new ArrayList<>();

			for (int x=0; x < 6; x++) {
				if (party.get(x) != null) {
					pokemons.add(party.get(x));
				}
			}

			try {
				ElgymsUtils.checkChampionRequirements(context.getSource().getPlayer(), pokemons);

				GymProvider.challengeChampion(context.getSource().getPlayer());
			} catch (Exception e) {
				context.getSource().sendMessage(Text.literal("§c" + e.getMessage()));
				return 1;
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
						"§b§lUsage:\n§3- champ challenge", context.getSource().isExecutedByPlayer()
				))
		);

		return 1;
	}
}
