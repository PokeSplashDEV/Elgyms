package org.pokesplash.elgyms.command.gyms.user;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.pokemon.Pokemon;
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
								".user.gym.challenge");
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
			return 1;
		}

		String gymId = StringArgumentType.getString(context, "gym");

		GymConfig gym = GymProvider.getGymById(gymId);

		if (gym == null) {
			context.getSource().sendMessage(Text.literal(Elgyms.lang.getPrefix() +
					"§cGym " + gymId + "§c does not exist."));
			return 1;
		}

		if (!GymProvider.getOpenGyms().contains(gym)) {
			context.getSource().sendMessage(Text.literal(Elgyms.lang.getPrefix() +
					"§cThe " + gym.getName() + "§c is currently closed."));
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
					"§cYou do not have the correct badges to challenge this gym."));
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
			ElgymsUtils.checkChallengerRequirements(context.getSource().getPlayer(), pokemons, gym);

			GymProvider.challengeGym(context.getSource().getPlayer(), gym);
		} catch (Exception e) {
			context.getSource().sendMessage(Text.literal("§c" + e.getMessage()));
			return 1;
		}

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
