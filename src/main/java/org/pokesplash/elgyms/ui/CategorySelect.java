package org.pokesplash.elgyms.ui;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.FlagType;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.page.GooeyPage;
import ca.landonjw.gooeylibs2.api.page.Page;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.config.CategoryConfig;
import org.pokesplash.elgyms.util.Utils;

/**
 * The menu to select a category. Opened with with /gyms
 */
public class CategorySelect {
	public Page getPage() {

		ChestTemplate.Builder template = ChestTemplate.builder(Elgyms.menu.getCategoryRows())
				.fill(Components.filler());

		for (CategoryConfig category : Elgyms.config.getCategories()) {

			template.set(category.getDisplaySlot(), GooeyButton.builder()
					.title(category.getName())
					.display(Utils.parseItemId(category.getDisplayItem()))
					.hideFlags(FlagType.All)
					.onClick(e -> {
						UIManager.openUIForcefully(e.getPlayer(), new GymSelect().getPage(category));
					})
					.build());
		}

		return GooeyPage.builder()
				.template(template.build())
				.title(Elgyms.lang.getTitle())
				.build();
	}
}
