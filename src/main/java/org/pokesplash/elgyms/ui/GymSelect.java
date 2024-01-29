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
import org.pokesplash.elgyms.config.CategoryConfig;
import org.pokesplash.elgyms.gym.Badge;
import org.pokesplash.elgyms.gym.GymConfig;
import org.pokesplash.elgyms.provider.BadgeProvider;
import org.pokesplash.elgyms.provider.GymProvider;
import org.pokesplash.elgyms.type.Type;
import org.pokesplash.elgyms.util.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class GymSelect {
	public Page getPage(CategoryConfig category, ServerPlayerEntity player) {

		ArrayList<Button> gymButtons = new ArrayList<>();
		for (GymConfig gym : GymProvider.getGymsByCategory(category.getName())) {

			Collection<String> lore = new ArrayList<>();
			StringBuilder base = new StringBuilder("§7( ");
			for (Type type : gym.getTypes()) {
				base.append(Elgyms.menu.getType(type) == null ? type : Elgyms.menu.getType(type)).append(" ");
			}
			base.append("§7)");
			lore.add(base.toString());

			boolean hasBeaten = false;

			ArrayList<Badge> playerBadges = BadgeProvider.getBadges(player).getBadgeIDs(category);

			// Checks the player has beaten the gym.
			for (Badge badge : playerBadges) {
                if (badge.getId().equals(gym.getBadge().getId())) {
                    hasBeaten = true;
                    break;
                }
			}

			boolean hasRequirements = false;

			if (hasBeaten) {
				lore.add(Elgyms.menu.getCompleted());
			} else {

				if (gym.getRequirements().getRequiredBadgeIDs().isEmpty()) {
					hasRequirements = true;
				} else {
					for (UUID id : gym.getRequirements().getRequiredBadgeIDs()) {
						if (BadgeProvider.getBadges(player).containsBadge(id)) {
							hasRequirements = true;
							break;
						}
					}
				}

				if (hasRequirements) {
					lore.add(Elgyms.menu.getIncompleted());
				} else {

					ArrayList<Badge> requiredBadges = new ArrayList<>();

					for (UUID id : gym.getRequirements().getRequiredBadgeIDs()) {
						GymConfig requiredGym = GymProvider.getGymFromBadge(id);

						if (requiredGym != null) {
							requiredBadges.add(requiredGym.getBadge());
						}
					}

					lore.add(Utils.formatPlaceholders(Elgyms.menu.getRequirements(),
							requiredBadges, gym.getBadge(), player, category, gym,
							BadgeProvider.getBadges(player).getCooldown(gym)));
				}
			}

			boolean finalHasRequirements = hasRequirements;
			gymButtons.add(GooeyButton.builder()
					.title(gym.getName())
					.display(Utils.parseItemId(gym.getDisplayItem()))
					.lore(lore)
					.hideFlags(FlagType.All)
					.onClick(e -> {
						if (!hasBeaten && finalHasRequirements) {
							UIManager.openUIForcefully(e.getPlayer(), new GymInfo().getPage(gym, category, e.getPlayer()));
						}
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
