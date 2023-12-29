package org.pokesplash.elgyms.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.pokesplash.elgyms.command.gyms.BadgeCommand;
import org.pokesplash.elgyms.command.gyms.BaseCommand;

public abstract class CommandHandler {
	public static final String basePermission = "elgyms";
	public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
		new BaseCommand().register(dispatcher);
		new BadgeCommand().register(dispatcher);
	}

	public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher,
	                                    CommandRegistryAccess commandBuildContext,
	                                    CommandManager.RegistrationEnvironment commandSelection) {
		registerCommands(dispatcher);
	}
}
