package org.pokesplash.elgyms.command.gyms.admin;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.command.CommandHandler;
import org.pokesplash.elgyms.gym.GymConfig;
import org.pokesplash.elgyms.provider.GymProvider;
import org.pokesplash.elgyms.util.LuckPermsUtils;
import org.pokesplash.elgyms.util.Utils;

public class EditGym {
	public LiteralCommandNode<ServerCommandSource> build() {
		return CommandManager.literal("edit")
				.requires(ctx -> {
					if (ctx.isExecutedByPlayer()) {
						return LuckPermsUtils.hasPermission(ctx.getPlayer(), CommandHandler.basePermission +
								".admin.edit");
					} else {
						return true;
					}
				})
				.executes(this::usage)
				.then(CommandManager.argument("name", StringArgumentType.string())
						.suggests((ctx, builder) -> {
							for (GymConfig gym : GymProvider.getGyms().values()) {
								builder.suggest(gym.getId());
							}
							return builder.buildFuture();
						})
						.executes(this::usage)
						.then(CommandManager.literal("item")
								.executes(this::usage)
								.then(CommandManager.argument("item", StringArgumentType.greedyString())
										.executes(this::item)))
						.then(CommandManager.literal("name")
								.executes(this::usage)
								.then(CommandManager.argument("newName", StringArgumentType.greedyString())
										.executes(this::name)))
						// Add then here
				).build();
	}

	public int name(CommandContext<ServerCommandSource> context) {

		String name = StringArgumentType.getString(context, "name");

		String newName = StringArgumentType.getString(context, "newName");

		GymConfig gym = GymProvider.getGymById(GymConfig.nameToId(name));

		if (gym == null) {
			context.getSource().sendMessage(Text.literal(Utils.formatMessage(
					"§cGym " + name + " doesn't exists.", context.getSource().isExecutedByPlayer()
			)));
			return 1;
		}

		gym.setName(newName);

		context.getSource().sendMessage(Text.literal(Utils.formatMessage(
				"§aSuccessfully changed the name from " + name + " to " + newName,
				context.getSource().isExecutedByPlayer()
		)));

		return 1;
	}

	public int item(CommandContext<ServerCommandSource> context) {

		String name = StringArgumentType.getString(context, "name");

		String itemArgument = StringArgumentType.getString(context, "item");
		ItemStack item = Utils.parseItemId(itemArgument);

		GymConfig gym = GymProvider.getGymById(GymConfig.nameToId(name));

		if (gym == null) {
			context.getSource().sendMessage(Text.literal(Utils.formatMessage(
					"§cGym " + name + " doesn't exists.", context.getSource().isExecutedByPlayer()
			)));
			return 1;
		}

		if (item.getItem().equals(Items.AIR)) {
			context.getSource().sendMessage(Text.literal(Utils.formatMessage(
					"§c" + itemArgument + " is not an item.", context.getSource().isExecutedByPlayer()
			)));
			return 1;
		}

		gym.setDisplayItem(itemArgument);

		context.getSource().sendMessage(Text.literal(Utils.formatMessage(
				"§aSuccessfully changed the gym item for " + name, context.getSource().isExecutedByPlayer()
		)));

		return 1;
	}

	public int usage(CommandContext<ServerCommandSource> context) {
		context.getSource().sendMessage(
				Text.literal(
						Elgyms.lang.getPrefix() +
								Utils.formatMessage(
										"§b§lUsage:\n§3- gym edit <field> <value>",
										context.getSource().isExecutedByPlayer()
								))
		);
		return 1;
	}
}
