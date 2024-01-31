package org.pokesplash.elgyms.command.champion;

import ca.landonjw.gooeylibs2.api.UIManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.pokesplash.elgyms.command.CommandHandler;
import org.pokesplash.elgyms.command.champion.champion.Accept;
import org.pokesplash.elgyms.command.champion.champion.Give;
import org.pokesplash.elgyms.command.champion.champion.Quit;
import org.pokesplash.elgyms.command.champion.champion.Reject;
import org.pokesplash.elgyms.command.champion.user.Challenge;
import org.pokesplash.elgyms.ui.ChampionInfo;
import org.pokesplash.elgyms.util.LuckPermsUtils;

public class ChampionCommand {
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		LiteralArgumentBuilder<ServerCommandSource> root = CommandManager
				.literal("champion")
				.requires(ctx -> {
					if (ctx.isExecutedByPlayer()) {
						return LuckPermsUtils.hasPermission(ctx.getPlayer(),
								CommandHandler.basePermission + ".user.champion");
					} else {
						return true;
					}
				})
				.executes(this::run);

		LiteralCommandNode<ServerCommandSource> registeredCommand = dispatcher.register(root);

		dispatcher.register(CommandManager.literal("champ").requires(
				ctx -> {
					if (ctx.isExecutedByPlayer()) {
						return LuckPermsUtils.hasPermission(ctx.getPlayer(),
								CommandHandler.basePermission + ".user.champion");
					} else {
						return true;
					}
				})
				.redirect(registeredCommand).executes(this::run));

		registeredCommand.addChild(new Give().build());
		registeredCommand.addChild(new Quit().build());
		registeredCommand.addChild(new Challenge().build());
		registeredCommand.addChild(new Accept().build());
		registeredCommand.addChild(new Reject().build());
	}

	public int run(CommandContext<ServerCommandSource> context) {

		if (!context.getSource().isExecutedByPlayer()) {
			context.getSource().sendMessage(Text.literal("This command must be ran by a player"));
			return 1;
		}

		UIManager.openUIForcefully(context.getSource().getPlayer(),
				new ChampionInfo().getPage(context.getSource().getPlayer()));

		return 1;
	}
}
