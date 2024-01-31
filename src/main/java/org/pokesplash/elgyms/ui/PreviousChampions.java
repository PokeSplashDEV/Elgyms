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
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.badge.PlayerBadges;
import org.pokesplash.elgyms.champion.ChampionHistoryItem;
import org.pokesplash.elgyms.config.CategoryConfig;
import org.pokesplash.elgyms.gym.GymConfig;
import org.pokesplash.elgyms.gym.Leader;
import org.pokesplash.elgyms.provider.BadgeProvider;
import org.pokesplash.elgyms.util.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class PreviousChampions {
	public Page getPage() {
		ArrayList<Button> buttons = new ArrayList<>();
		for (ChampionHistoryItem champ : Elgyms.championHistory.getHistory()) {
			ArrayList<String> lore = new ArrayList<>();
			DateFormat format = new SimpleDateFormat("d MMM yyyy");
			Date startDate = new Date(champ.getStartDate());
			Date endDate = new Date(champ.getEndDate());
			lore.add("§bFrom: " + format.format(startDate));
			lore.add("§bTo: " + format.format(endDate));

			PlayerBadges champBadges = BadgeProvider.getBadges(champ.getUuid());

			// If no champ could be found, just skip.
			if (champBadges == null) {
				continue;
			}

			buttons.add(
					GooeyButton.builder()
							.title(champBadges.getName())
							.display(Utils.getPlayerHead(champBadges.getName()))
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
					UIManager.openUIForcefully(e.getPlayer(), new ChampionInfo().getPage(e.getPlayer()));
				}))
				.build();

		LinkedPage page = PaginationHelper.createPagesFromPlaceholders(template, buttons, null);
		page.setTitle("Previous Champions");

		return page;
	}
}
