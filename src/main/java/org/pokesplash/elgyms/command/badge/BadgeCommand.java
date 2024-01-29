package org.pokesplash.elgyms.command.badge;

import ca.landonjw.gooeylibs2.api.UIManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.pokesplash.elgyms.command.CommandHandler;
import org.pokesplash.elgyms.command.badge.leader.GiveBadge;
import org.pokesplash.elgyms.command.badge.leader.OtherBadge;
import org.pokesplash.elgyms.command.badge.leader.RemoveBadge;
import org.pokesplash.elgyms.provider.BadgeProvider;
import org.pokesplash.elgyms.util.LuckPermsUtils;

public class BadgeCommand {
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		LiteralArgumentBuilder<ServerCommandSource> root = CommandManager
				.literal("badges")
				.requires(ctx -> {
					if (ctx.isExecutedByPlayer()) {
						return LuckPermsUtils.hasPermission(ctx.getPlayer(), CommandHandler.basePermission +
								".badges.base");
					} else {
						return true;
					}
				})
				.executes(this::run);

		LiteralCommandNode<ServerCommandSource> registeredCommand = dispatcher.register(root);

		dispatcher.register(CommandManager.literal("badge").requires(
				ctx -> {
					if (ctx.isExecutedByPlayer()) {
						return LuckPermsUtils.hasPermission(ctx.getPlayer(), CommandHandler.basePermission +
								".badges.base");
					} else {
						return true;
					}
				})
				.redirect(registeredCommand).executes(this::run));

		registeredCommand.addChild(new OtherBadge().build());
		registeredCommand.addChild(new GiveBadge().build());
		registeredCommand.addChild(new RemoveBadge().build());
	}

	public int run(CommandContext<ServerCommandSource> context) {
		if (!context.getSource().isExecutedByPlayer()) {
			context.getSource().sendMessage(Text.literal("This command must be ran by a player"));
			return 1;
		}

		try {
			UIManager.openUIForcefully(context.getSource().getPlayer(), new org.pokesplash.elgyms.ui.Badges()
					.getPage(BadgeProvider.getBadges(context.getSource().getPlayer()), false));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 1;

	}
}
