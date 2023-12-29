package org.pokesplash.elgyms.ui;

import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.button.FlagType;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.button.PlaceholderButton;
import ca.landonjw.gooeylibs2.api.helpers.PaginationHelper;
import ca.landonjw.gooeylibs2.api.page.LinkedPage;
import ca.landonjw.gooeylibs2.api.page.Page;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.badge.PlayerBadges;
import org.pokesplash.elgyms.config.CategoryConfig;
import org.pokesplash.elgyms.gym.Badge;
import org.pokesplash.elgyms.util.Utils;

import java.util.ArrayList;

public class Badges {
	public Page getPage(PlayerBadges badges, boolean isOther) {

		ArrayList<Badge> badgeIds = new ArrayList<>();

		for (CategoryConfig categoryConfig : Elgyms.config.getCategories()) {
			badgeIds.addAll(badges.getBadgeIDs(categoryConfig));
		}

		ArrayList<Button> buttons = new ArrayList<>();
		for (Badge badge : badgeIds) {
			buttons.add(
					GooeyButton.builder()
							.title(badge.getName())
							.display(Utils.parseItemId(badge.getMaterial()))
							.hideFlags(FlagType.All)
							.build()
			);
		}

		int rows = buttons.isEmpty() ? 3 : (int) Math.ceil((double) buttons.size() / 7) + 2;

		ChestTemplate template = ChestTemplate.builder(rows)
				.rectangle(1, 1, rows - 2, 7, new PlaceholderButton())
				.fill(Components.filler())
				.build();

		LinkedPage page = PaginationHelper.createPagesFromPlaceholders(template, buttons, null);
		page.setTitle(Elgyms.menu.getBadgeTitle() + (isOther ? " - " + badges.getName() : ""));

		return page;
	}
}
