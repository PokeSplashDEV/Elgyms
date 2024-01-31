package org.pokesplash.elgyms.command.gyms.leader.gym;

import ca.landonjw.gooeylibs2.api.UIManager;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.command.CommandHandler;
import org.pokesplash.elgyms.gym.GymConfig;
import org.pokesplash.elgyms.provider.GymProvider;
import org.pokesplash.elgyms.ui.LeaderTeam;
import org.pokesplash.elgyms.util.LuckPermsUtils;
import org.pokesplash.elgyms.util.Utils;

public class Team {
	public LiteralCommandNode<ServerCommandSource> build() {
		return CommandManager.literal("team")
				.requires(ctx -> {
					if (ctx.isExecutedByPlayer()) {
						return LuckPermsUtils.hasPermission(ctx.getPlayer(), CommandHandler.basePermission +
								".leader.gym.team");
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
			}

			String gymId = StringArgumentType.getString(context, "gym");

			GymConfig gym = GymProvider.getGymById(gymId);

			if (gym == null) {
				context.getSource().sendMessage(Text.literal(Elgyms.lang.getPrefix() +
						"§cGym " + gymId + " does not exist."));
				return 1;
			}

			if (!gym.containsLeader(context.getSource().getPlayer().getUuid())) {
				context.getSource().sendMessage(Text.literal(Elgyms.lang.getPrefix() +
						"§cYou are not a leader of this gym."));
				return 1;
			}

			UIManager.openUIForcefully(context.getSource().getPlayer(),
					new LeaderTeam().getPage(context.getSource().getPlayer(), gym));
		} catch (Exception e) {
			context.getSource().sendMessage(Text.literal("§c" + e.getMessage()));
			Elgyms.LOGGER.error(e.getStackTrace());
		}

		return 1;
	}

	public int usage(CommandContext<ServerCommandSource> context) {
		context.getSource().sendMessage(
				Text.literal(
						Elgyms.lang.getPrefix() +
						Utils.formatMessage(
						"§b§lUsage:\n§3- gym team <gym>", context.getSource().isExecutedByPlayer()
				))
		);

		return 1;
	}
}
