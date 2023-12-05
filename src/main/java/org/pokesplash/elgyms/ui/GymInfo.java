package org.pokesplash.elgyms.ui;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.button.FlagType;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.page.GooeyPage;
import ca.landonjw.gooeylibs2.api.page.Page;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.config.CategoryConfig;
import org.pokesplash.elgyms.gym.GymConfig;
import org.pokesplash.elgyms.util.ElgymsUtils;
import org.pokesplash.elgyms.util.Utils;

public class GymInfo {
	public Page getPage(GymConfig gym, CategoryConfig categoryConfig) {
		Button challenge = GooeyButton.builder()
				.title(Elgyms.menu.getChallengeButtonTitle())
				.display(Utils.parseItemId(Elgyms.menu.getChallengeButtonMaterial()))
				.hideFlags(FlagType.All)
				.onClick(e -> {
					// TODO challenge the gym.
				})
				.build();

		Button rules = GooeyButton.builder()
				.title(Elgyms.menu.getRulesTitle())
				.display(Utils.parseItemId(Elgyms.menu.getRulesButtonMaterial()))
				.hideFlags(FlagType.All)
				.lore(ElgymsUtils.getRulesLore(gym))
				.build();

		ChestTemplate template = ChestTemplate.builder(3)
				.fill(Components.filler())
				.set(10, challenge)
				.set(13, rules)
				.set(16, Components.backButton(e -> {
					UIManager.openUIForcefully(e.getPlayer(), new GymSelect().getPage(categoryConfig, e.getPlayer()));
				}))
				.build();

		return GooeyPage.builder()
				.template(template)
				.title(gym.getName())
				.build();
	}
}
