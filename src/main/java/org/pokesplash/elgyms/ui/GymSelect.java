package org.pokesplash.elgyms.ui;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.button.FlagType;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.button.PlaceholderButton;
import ca.landonjw.gooeylibs2.api.helpers.PaginationHelper;
import ca.landonjw.gooeylibs2.api.page.GooeyPage;
import ca.landonjw.gooeylibs2.api.page.LinkedPage;
import ca.landonjw.gooeylibs2.api.page.Page;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.config.CategoryConfig;
import org.pokesplash.elgyms.gym.GymConfig;
import org.pokesplash.elgyms.provider.GymProvider;
import org.pokesplash.elgyms.type.Type;
import org.pokesplash.elgyms.util.Utils;

import java.util.ArrayList;
import java.util.Collection;

public class GymSelect {
	public Page getPage(CategoryConfig category) {

		ArrayList<Button> gymButtons = new ArrayList<>();
		for (GymConfig gym : GymProvider.getGymsByCategory(category.getName())) {

			Collection<String> lore = new ArrayList<>();
			StringBuilder base = new StringBuilder("§7( ");
			for (Type type : gym.getTypes()) {
				base.append(Elgyms.lang.getType(type)).append(" ");
			}
			base.append("§7)");
			lore.add(base.toString());

			gymButtons.add(GooeyButton.builder()
					.title(gym.getName())
					.display(Utils.parseItemId(gym.getDisplayItem()))
					.lore(lore)
					.hideFlags(FlagType.All)
					.onClick(e -> {
						// TODO open gym menu
					})
					.build());
		}

		int rows = (int) Math.ceil((double) gymButtons.size() / 7) + 2;

		ChestTemplate template = ChestTemplate.builder(rows)
				.rectangle(1, 1, rows - 2, 7, new PlaceholderButton())
				.fill(Components.filler())
				.set(Elgyms.menu.getBackButtonPosition(), Components.backButton(e -> {
					UIManager.openUIForcefully(e.getPlayer(), new CategorySelect().getPage());
				}))
				.build();

		LinkedPage page = PaginationHelper.createPagesFromPlaceholders(template, gymButtons, null);
		page.setTitle(category.getName());

		return page;
	}
}
