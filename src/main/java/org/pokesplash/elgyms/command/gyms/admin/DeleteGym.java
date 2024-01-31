package org.pokesplash.elgyms.command.gyms.admin;

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
import org.pokesplash.elgyms.util.LuckPermsUtils;
import org.pokesplash.elgyms.util.Utils;

public class DeleteGym {
	public LiteralCommandNode<ServerCommandSource> build() {
		return CommandManager.literal("delete")
				.requires(ctx -> {
					if (ctx.isExecutedByPlayer()) {
						return LuckPermsUtils.hasPermission(ctx.getPlayer(), CommandHandler.basePermission +
								".admin.gym.delete");
					} else {
						return true;
					}
				})
				.executes(this::usage)
				.then(CommandManager.argument("name", StringArgumentType.greedyString())
						.suggests((ctx, builder) -> {
							for (GymConfig gym : GymProvider.getGyms().values()) {
								builder.suggest(gym.getName());
							}
							return builder.buildFuture();
						})
						.executes(this::run)
				).build();
	}

	public int run(CommandContext<ServerCommandSource> context) {

		String name = StringArgumentType.getString(context, "name");

		GymConfig gym = GymProvider.getGymById(GymConfig.nameToId(name));

		if (gym == null) {
			context.getSource().sendMessage(Text.literal(Utils.formatMessage(
					"§cGym " + name + " doesn't exists.", context.getSource().isExecutedByPlayer()
			)));
			return 1;
		}

		GymProvider.deleteGym(gym);

		context.getSource().sendMessage(Text.literal(Utils.formatMessage(
				"§aDeleted gym: " + name, context.getSource().isExecutedByPlayer()
		)));

		return 1;
	}

	public int usage(CommandContext<ServerCommandSource> context) {
		context.getSource().sendMessage(
				Text.literal(
						Elgyms.lang.getPrefix() +
								Utils.formatMessage(
										"§b§lUsage:\n§3- gym delete <gym>",
										context.getSource().isExecutedByPlayer()
								))
		);
		return 1;
	}
}
