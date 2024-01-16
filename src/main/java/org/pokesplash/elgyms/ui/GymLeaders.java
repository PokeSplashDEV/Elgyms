package org.pokesplash.elgyms.ui;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.button.FlagType;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.button.PlaceholderButton;
import ca.landonjw.gooeylibs2.api.helpers.PaginationHelper;
import ca.landonjw.gooeylibs2.api.page.LinkedPage;
import ca.landonjw.gooeylibs2.api.page.Page;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.badge.PlayerBadges;
import org.pokesplash.elgyms.config.CategoryConfig;
import org.pokesplash.elgyms.gym.GymConfig;
import org.pokesplash.elgyms.gym.Leader;
import org.pokesplash.elgyms.provider.BadgeProvider;
import org.pokesplash.elgyms.provider.GymProvider;
import org.pokesplash.elgyms.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class GymLeaders {
	public Page getPage(GymConfig gym) {

		// Get a map of all leader UUIDs and their names.
		HashMap<UUID, String> gymLeaders = new HashMap<>();
		for (Leader leader : gym.getLeaders()) {
			PlayerBadges badges = BadgeProvider.getBadges(leader.getUuid());

			if (badges == null) {
				continue;
			}

			gymLeaders.put(leader.getUuid(), badges.getName());
		}


		ArrayList<Button> buttons = new ArrayList<>();
		for (UUID leader : gymLeaders.keySet()) {
			ArrayList<String> lore = new ArrayList<>();

			ServerPlayerEntity leaderPlayer = Elgyms.server.getPlayerManager().getPlayer(leader);

			if (leaderPlayer != null) {
				lore.add("§a(Online)");
			} else {
				lore.add("§c(Offline)");
			}

			buttons.add(
					GooeyButton.builder()
							.title(gymLeaders.get(leader))
							.display(Utils.getPlayerHead(gymLeaders.get(leader)))
							.hideFlags(FlagType.All)
							.lore(lore)
							.build()
			);
		}

		int rows = buttons.isEmpty() ? 3 : (int) Math.ceil((double) buttons.size() / 7) + 2;

		ChestTemplate template = ChestTemplate.builder(rows)
				.rectangle(1, 1, rows - 2, 7, new PlaceholderButton())
				.fill(Components.filler())
				.set(0, Components.backButton(e -> {

					CategoryConfig categoryConfig = Elgyms.config.getCategoryByName(gym.getCategoryName());


					UIManager.openUIForcefully(e.getPlayer(), new GymInfo().getPage(
							gym, categoryConfig, e.getPlayer()
					));
				}))
				.build();

		LinkedPage page = PaginationHelper.createPagesFromPlaceholders(template, buttons, null);
		page.setTitle("Leaders");

		return page;
	}
}
