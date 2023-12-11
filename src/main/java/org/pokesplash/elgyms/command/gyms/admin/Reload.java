package org.pokesplash.elgyms.command.gyms.admin;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.command.CommandHandler;
import org.pokesplash.elgyms.util.LuckPermsUtils;

public class Reload {
	public LiteralCommandNode<ServerCommandSource> build() {
		return CommandManager.literal("reload")
				.requires(ctx -> {
					if (ctx.isExecutedByPlayer()) {
						return LuckPermsUtils.hasPermission(ctx.getPlayer(), CommandHandler.basePermission +
								".admin.reload");
					} else {
						return true;
					}
				})
				.executes(this::run)
				.build();
	}

	public int run(CommandContext<ServerCommandSource> context) {

		Elgyms.load();

		context.getSource().sendMessage(Text.literal("Config reloaded."));

		return 1;
	}
}
