package org.pokesplash.elgyms.command.gyms;

import ca.landonjw.gooeylibs2.api.UIManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.command.CommandHandler;
import org.pokesplash.elgyms.command.gyms.admin.*;
import org.pokesplash.elgyms.command.gyms.leader.challenge.Accept;
import org.pokesplash.elgyms.command.gyms.leader.cooldown.GiveCooldown;
import org.pokesplash.elgyms.command.gyms.leader.cooldown.RemoveCooldown;
import org.pokesplash.elgyms.command.gyms.leader.gym.Close;
import org.pokesplash.elgyms.command.gyms.leader.gym.Open;
import org.pokesplash.elgyms.command.gyms.leader.challenge.Reject;
import org.pokesplash.elgyms.command.gyms.leader.gym.Queue;
import org.pokesplash.elgyms.command.gyms.leader.gym.Team;
import org.pokesplash.elgyms.command.gyms.user.Challenge;
import org.pokesplash.elgyms.command.gyms.user.Spectate;
import org.pokesplash.elgyms.ui.CategorySelect;
import org.pokesplash.elgyms.util.LuckPermsUtils;

public class BaseCommand {
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		LiteralArgumentBuilder<ServerCommandSource> root = CommandManager
				.literal("elgyms")
				.requires(ctx -> {
					if (ctx.isExecutedByPlayer()) {
						return LuckPermsUtils.hasPermission(ctx.getPlayer(), CommandHandler.basePermission + ".user");
					} else {
						return true;
					}
				})
				.executes(this::run);

		LiteralCommandNode<ServerCommandSource> registeredCommand = dispatcher.register(root);

		dispatcher.register(CommandManager.literal("gyms").requires(
				ctx -> {
					if (ctx.isExecutedByPlayer()) {
						return LuckPermsUtils.hasPermission(ctx.getPlayer(), CommandHandler.basePermission + ".user");
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
		registeredCommand.addChild(new Accept().build());
		registeredCommand.addChild(new Reject().build());
		registeredCommand.addChild(new Open().build());
		registeredCommand.addChild(new Close().build());
		registeredCommand.addChild(new GiveCooldown().build());
		registeredCommand.addChild(new RemoveCooldown().build());
		registeredCommand.addChild(new Queue().build());
		registeredCommand.addChild(new Position().build());
		registeredCommand.addChild(new Spectate().build());
		registeredCommand.addChild(new Team().build());
		registeredCommand.addChild(new GetTeam().build());
	}

	public int run(CommandContext<ServerCommandSource> context) {

		try {
			if (!context.getSource().isExecutedByPlayer()) {
				context.getSource().sendMessage(Text.literal("This command must be ran by a player"));
				return 1;
			}

			UIManager.openUIForcefully(context.getSource().getPlayer(), new CategorySelect().getPage());
		}
		catch (Exception e) {
			context.getSource().sendMessage(Text.literal("§cSomething went wrong."));
			Elgyms.LOGGER.error(e.getStackTrace());
		}

		return 1;
	}
}
