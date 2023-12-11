package org.pokesplash.elgyms.command.gyms.admin;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.command.CommandHandler;
import org.pokesplash.elgyms.config.CategoryConfig;
import org.pokesplash.elgyms.gym.GymConfig;
import org.pokesplash.elgyms.provider.GymProvider;
import org.pokesplash.elgyms.util.LuckPermsUtils;
import org.pokesplash.elgyms.util.Utils;

public class CreateGym {
	public LiteralCommandNode<ServerCommandSource> build() {
		return CommandManager.literal("create")
				.requires(ctx -> {
					if (ctx.isExecutedByPlayer()) {
						return LuckPermsUtils.hasPermission(ctx.getPlayer(), CommandHandler.basePermission +
								".admin.create");
					} else {
						return true;
					}
				})
				.executes(this::usage)
				.then(CommandManager.argument("category", StringArgumentType.string())
						.suggests((ctx, builder) -> {
							for (CategoryConfig categoryConfig : Elgyms.config.getCategories()) {
								builder.suggest(categoryConfig.getName());
							}
							return builder.buildFuture();
						})
						.executes(this::usage)
						.then(CommandManager.argument("name", StringArgumentType.greedyString())
								.executes(this::run)
						)).build();
	}

	public int run(CommandContext<ServerCommandSource> context) {

		String name = StringArgumentType.getString(context, "name");

		String category = StringArgumentType.getString(context, "category");

		boolean categoryExists = false;
		for (CategoryConfig categoryConfig : Elgyms.config.getCategories()) {
			if (categoryConfig.getName().equalsIgnoreCase(category)) {
				categoryExists = true;
				break;
			}
		}

		if (!categoryExists) {
			context.getSource().sendMessage(Text.literal(Utils.formatMessage(
					"§cCould not find category: " + category, context.getSource().isExecutedByPlayer()
			)));
			return 1;
		}

		if (GymProvider.getGymById(GymConfig.nameToId(name)) != null) {
			context.getSource().sendMessage(Text.literal(Utils.formatMessage(
					"§cGym " + name + " already exists.", context.getSource().isExecutedByPlayer()
			)));
			return 1;
		}

		GymConfig newGym = new GymConfig(name, category);
		newGym.write();
		GymProvider.addGym(newGym);

		context.getSource().sendMessage(Text.literal(Utils.formatMessage(
				"§aCreated gym: " + name, context.getSource().isExecutedByPlayer()
		)));

		return 1;
	}

	public int usage(CommandContext<ServerCommandSource> context) {
		context.getSource().sendMessage(
				Text.literal(
						Elgyms.lang.getPrefix() +
								Utils.formatMessage(
										"§b§lUsage:\n§3- gym create <category> <gym>",
										context.getSource().isExecutedByPlayer()
								))
		);
		return 1;
	}
}
