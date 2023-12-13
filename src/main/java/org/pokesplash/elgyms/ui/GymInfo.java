package org.pokesplash.elgyms.ui;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.button.FlagType;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.page.GooeyPage;
import ca.landonjw.gooeylibs2.api.page.Page;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.text.Text;
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.config.CategoryConfig;
import org.pokesplash.elgyms.gym.GymConfig;
import org.pokesplash.elgyms.provider.GymProvider;
import org.pokesplash.elgyms.util.ElgymsUtils;
import org.pokesplash.elgyms.util.Utils;

import java.util.ArrayList;

public class GymInfo {
	public Page getPage(GymConfig gym, CategoryConfig categoryConfig) {
		Button challenge = GooeyButton.builder()
				.title(Elgyms.menu.getChallengeButtonTitle())
				.display(Utils.parseItemId(Elgyms.menu.getChallengeButtonMaterial()))
				.hideFlags(FlagType.All)
				.onClick(e -> {
					PlayerPartyStore party = Cobblemon.INSTANCE.getStorage().getParty(e.getPlayer());

					ArrayList<Pokemon> pokemons = new ArrayList<>();

					for (int x=0; x < 6; x++) {
						if (party.get(x) != null) {
							pokemons.add(party.get(x));
						}
					}

					try {
						ElgymsUtils.checkChallengerRequirements(pokemons, gym);

						GymProvider.challengeGym(e.getPlayer(), gym);
					} catch (Exception ex) {
						e.getPlayer().sendMessage(Text.literal("§c" + ex.getMessage()));
					}
				})
				.build();

		Button closed = GooeyButton.builder()
				.title(Elgyms.menu.getClosedTitle())
				.display(Utils.parseItemId(Elgyms.menu.getClosedButtonMaterial()))
				.hideFlags(FlagType.All)
				.build();

		Button rules = GooeyButton.builder()
				.title(Elgyms.menu.getRulesTitle())
				.display(Utils.parseItemId(Elgyms.menu.getRulesButtonMaterial()))
				.hideFlags(FlagType.All)
				.lore(ElgymsUtils.getRulesLore(gym))
				.build();

		ChestTemplate template = ChestTemplate.builder(3)
				.fill(Components.filler())
				.set(10, GymProvider.getOpenGyms().contains(gym) ? challenge : closed)
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
