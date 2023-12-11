package org.pokesplash.elgyms;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pokesplash.elgyms.champion.ChampionConfig;
import org.pokesplash.elgyms.champion.ChampionHistory;
import org.pokesplash.elgyms.champion.ChampionHistoryItem;
import org.pokesplash.elgyms.command.CommandHandler;
import org.pokesplash.elgyms.config.Config;
import org.pokesplash.elgyms.config.Lang;
import org.pokesplash.elgyms.event.PlayerJoinEvent;
import org.pokesplash.elgyms.event.PlayerLeaveEvent;
import org.pokesplash.elgyms.gym.Leader;
import org.pokesplash.elgyms.provider.BadgeProvider;
import org.pokesplash.elgyms.provider.GymProvider;
import org.pokesplash.elgyms.ui.config.MenuConfig;
import org.pokesplash.elgyms.util.Utils;

import javax.sql.ConnectionEvent;
import java.util.UUID;

public class Elgyms implements ModInitializer {
	public static final String MOD_ID = "Elgyms";
	public static final String BASE_PATH = "/config/" + MOD_ID + "/";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	public static final Config config = new Config();
	public static final ChampionHistory championHistory = new ChampionHistory();
	public static final MenuConfig menu = new MenuConfig();
	public static final Lang lang = new Lang();
	public static MinecraftServer server;

	/**
	 * Runs the mod initializer.
	 */
	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register(CommandHandler::registerCommands);
		ServerPlayConnectionEvents.JOIN.register(new PlayerJoinEvent());
		ServerPlayConnectionEvents.DISCONNECT.register(new PlayerLeaveEvent());
		ServerWorldEvents.LOAD.register((t, e) -> server = t);
		load();
	}

	public static void load() {
		GymProvider.init();
		config.init();
		championHistory.init();
		lang.init();
		menu.init();
		BadgeProvider.init();
	}
}
