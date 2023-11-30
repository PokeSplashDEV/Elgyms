package org.pokesplash.elgyms.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public abstract class CommandHandler {
	public static final String basePermission = "elgyms";
	public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
		new BaseCommand().register(dispatcher);
	}

	public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher,
	                                    CommandRegistryAccess commandBuildContext,
	                                    CommandManager.RegistrationEnvironment commandSelection) {
		registerCommands(dispatcher);
	}
}
