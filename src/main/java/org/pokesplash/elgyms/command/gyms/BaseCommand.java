package org.pokesplash.elgyms.command.gyms;

import ca.landonjw.gooeylibs2.api.UIManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.pokesplash.elgyms.command.CommandHandler;
import org.pokesplash.elgyms.command.gyms.admin.*;
import org.pokesplash.elgyms.command.gyms.user.Challenge;
import org.pokesplash.elgyms.ui.CategorySelect;
import org.pokesplash.elgyms.util.LuckPermsUtils;

public class BaseCommand {
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		LiteralArgumentBuilder<ServerCommandSource> root = CommandManager
				.literal("elgyms")
				.requires(ctx -> {
					if (ctx.isExecutedByPlayer()) {
						return LuckPermsUtils.hasPermission(ctx.getPlayer(), CommandHandler.basePermission + ".base");
					} else {
						return true;
					}
				})
				.executes(this::run);

		LiteralCommandNode<ServerCommandSource> registeredCommand = dispatcher.register(root);

		dispatcher.register(CommandManager.literal("gyms").requires(
				ctx -> {
					if (ctx.isExecutedByPlayer()) {
						return LuckPermsUtils.hasPermission(ctx.getPlayer(), CommandHandler.basePermission + ".base");
					} else {
						return true;
					}
				})
				.redirect(registeredCommand).executes(this::run));

		dispatcher.register(CommandManager.literal("gym").requires(
						ctx -> {
							if (ctx.isExecutedByPlayer()) {
								return LuckPermsUtils.hasPermission(ctx.getPlayer(), CommandHandler.basePermission + ".base");
							} else {
								return true;
							}
						})
				.redirect(registeredCommand).executes(this::run));

		registeredCommand.addChild(new Reload().build());
		registeredCommand.addChild(new Challenge().build());
		registeredCommand.addChild(new CreateGym().build());
		registeredCommand.addChild(new DeleteGym().build());
		registeredCommand.addChild(new EditGym().build());
		registeredCommand.addChild(new Leader().build());
		registeredCommand.addChild(new SetTeam().build());

	}

	public int run(CommandContext<ServerCommandSource> context) {

		if (!context.getSource().isExecutedByPlayer()) {
			context.getSource().sendMessage(Text.literal("This command must be ran by a player"));
			return 1;
		}

		UIManager.openUIForcefully(context.getSource().getPlayer(), new CategorySelect().getPage());

		return 1;
	}
}