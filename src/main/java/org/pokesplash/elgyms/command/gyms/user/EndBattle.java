package org.pokesplash.elgyms.command.gyms.user;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import kotlin.Unit;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.badge.PlayerBadges;
import org.pokesplash.elgyms.battle.BattleData;
import org.pokesplash.elgyms.command.CommandHandler;
import org.pokesplash.elgyms.gym.GymConfig;
import org.pokesplash.elgyms.provider.BadgeProvider;
import org.pokesplash.elgyms.provider.GymProvider;
import org.pokesplash.elgyms.util.ElgymsUtils;
import org.pokesplash.elgyms.util.LuckPermsUtils;
import org.pokesplash.elgyms.util.Utils;

import java.util.ArrayList;
import java.util.UUID;

public class EndBattle {
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		LiteralArgumentBuilder<ServerCommandSource> root = CommandManager
				.literal("endbattle")
				.requires(ctx -> {
					if (ctx.isExecutedByPlayer()) {
						return LuckPermsUtils.hasPermission(ctx.getPlayer(), CommandHandler.basePermission +
								".endbattle");
					} else {
						return true;
					}
				})
				.executes(this::run);

		LiteralCommandNode<ServerCommandSource> registeredCommand = dispatcher.register(root);
	}

	public int run(CommandContext<ServerCommandSource> context) {

		if (!context.getSource().isExecutedByPlayer()) {
			context.getSource().sendMessage(Text.literal("This command must be executed by a player"));
			return 1;
		}

		PokemonBattle battle =
				Cobblemon.INSTANCE.getBattleRegistry().getBattleByParticipatingPlayer(context.getSource().getPlayer());

		if (battle == null) {
			context.getSource().sendMessage(Text.literal("§cYou are not in a battle."));
			return 1;
		}

		battle.end();

		// If its a gym battle, try return leader Pokemon.
		GymProvider.endGymBattle(battle.getBattleId());

		ArrayList<ServerPlayerEntity> players = new ArrayList<>(battle.getPlayers());

		for (ServerPlayerEntity player : players) {
			player.sendMessage(Text.literal("§2The battle was ended."));
		}

		return 1;
	}
}
