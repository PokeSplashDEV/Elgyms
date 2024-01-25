package org.pokesplash.elgyms.command.gyms.admin;

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
import org.pokesplash.elgyms.command.CommandHandler;
import org.pokesplash.elgyms.gym.GymConfig;
import org.pokesplash.elgyms.provider.BadgeProvider;
import org.pokesplash.elgyms.provider.GymProvider;
import org.pokesplash.elgyms.util.ElgymsUtils;
import org.pokesplash.elgyms.util.LuckPermsUtils;
import org.pokesplash.elgyms.util.Utils;

import java.util.ArrayList;

public class SetTeam {
	public LiteralCommandNode<ServerCommandSource> build() {
		return CommandManager.literal("setTeam")
				.requires(ctx -> {
					if (ctx.isExecutedByPlayer()) {
						return LuckPermsUtils.hasPermission(ctx.getPlayer(), CommandHandler.basePermission +
								".admin.teams");
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
						.executes(this::usage)
						.then(CommandManager.argument("gym", StringArgumentType.greedyString())
								.suggests((ctx, builder) -> {
									for (GymConfig gymConfig : GymProvider.getGyms().values()) {
										builder.suggest(gymConfig.getId());
									}
									return builder.buildFuture();
								})
								.executes(this::run)
						)).build();
	}

	public int run(CommandContext<ServerCommandSource> context) {

		if (!context.getSource().isExecutedByPlayer()) {
			context.getSource().sendMessage(Text.literal("This command must be ran by a player"));
			return 1;
		}

		String gym = StringArgumentType.getString(context, "gym");

		String player = StringArgumentType.getString(context, "player");

		PlayerBadges badges = BadgeProvider.getBadges(player);

		if (badges == null) {
			context.getSource().sendMessage(Text.literal(
					"§cPlayer " + player + "§c could not be found."
			));
			return 1;
		}

		GymConfig gymConfig = GymProvider.getGymById(GymConfig.nameToId(gym));

		if (gymConfig == null) {
			context.getSource().sendMessage(Text.literal(Utils.formatMessage(
					"§cGym " + gym + "§c could not be found.", context.getSource().isExecutedByPlayer()
			)));
			return 1;
		}

		if (gymConfig.getLeader(badges.getUuid()) == null) {
			context.getSource().sendMessage(Text.literal("§c" + player + " is not a leader of this gym."));
			return 1;
		}

		PlayerPartyStore party = Cobblemon.INSTANCE.getStorage().getParty(context.getSource().getPlayer());

		ArrayList<Pokemon> pokemons = new ArrayList<>();
		for (int x=0; x < 6; x++) {
			if (party.get(x) == null) {
				continue;
			}

			pokemons.add(party.get(x));
		}

		try {
			ElgymsUtils.checkLeaderRequirements(context.getSource().getPlayer(), pokemons, gymConfig);

			ArrayList<Pokemon> leaderPokemon = ElgymsUtils.setLevelAndPp(pokemons,
					gymConfig.getRequirements().getPokemonLevel());

			gymConfig.getLeader(badges.getUuid()).setTeam(leaderPokemon);
			gymConfig.write();

		} catch (Exception e) {
			context.getSource().sendMessage(Text.literal("§c" + e.getMessage()));
			return 1;
		}


		context.getSource().sendMessage(Text.literal(Utils.formatMessage(
				"§aAdded gym team for " + badges.getName() + "§a in " + gymConfig.getName(),
				context.getSource().isExecutedByPlayer()
		)));

		return 1;
	}

	public int usage(CommandContext<ServerCommandSource> context) {
		context.getSource().sendMessage(
				Text.literal(
						Elgyms.lang.getPrefix() +
								Utils.formatMessage(
										"§b§lUsage:\n§3- gym setTeam <player> <gym>",
										context.getSource().isExecutedByPlayer()
								))
		);
		return 1;
	}
}
