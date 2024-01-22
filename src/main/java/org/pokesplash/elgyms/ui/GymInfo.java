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
import net.minecraft.server.DataPackContents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.badge.PlayerBadges;
import org.pokesplash.elgyms.config.CategoryConfig;
import org.pokesplash.elgyms.gym.GymConfig;
import org.pokesplash.elgyms.provider.BadgeProvider;
import org.pokesplash.elgyms.provider.GymProvider;
import org.pokesplash.elgyms.util.ElgymsUtils;
import org.pokesplash.elgyms.util.Utils;

import java.util.ArrayList;
import java.util.Date;

public class GymInfo {
	public Page getPage(GymConfig gym, CategoryConfig categoryConfig, ServerPlayerEntity challenger) {
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
						ElgymsUtils.checkChallengerRequirements(challenger, pokemons, gym);

						GymProvider.challengeGym(e.getPlayer(), gym);
					} catch (Exception ex) {
						e.getPlayer().sendMessage(Text.literal("§c" + ex.getMessage()));
					}

					UIManager.openUIForcefully(e.getPlayer(), new GymInfo().getPage(gym, categoryConfig,
							challenger));
				})
				.build();

		Button cancel = GooeyButton.builder()
				.title(Elgyms.menu.getCancelChallengeButtonTitle())
				.display(Utils.parseItemId(Elgyms.menu.getCancelChallengeButtonMaterial()))
				.hideFlags(FlagType.All)
				.onClick(e -> {
					GymProvider.cancelChallenge(e.getPlayer().getUuid());

					e.getPlayer().sendMessage(Text.literal(Utils.formatPlaceholders(
							Elgyms.lang.getPrefix() + Elgyms.lang.getCancelChallenge(),
							null, gym.getBadge(), e.getPlayer(), categoryConfig, gym, null
					)));

					UIManager.openUIForcefully(e.getPlayer(), new GymInfo().getPage(gym, categoryConfig,
							challenger));
				})
				.build();

		Button closed = GooeyButton.builder()
				.title(Elgyms.menu.getClosedTitle())
				.display(Utils.parseItemId(Elgyms.menu.getClosedButtonMaterial()))
				.hideFlags(FlagType.All)
				.build();

		Button cooldown = GooeyButton.builder()
				.title(Utils.formatPlaceholders(Elgyms.menu.getCooldownTitle(),
						null, gym.getBadge(), challenger, categoryConfig, gym,
						BadgeProvider.getBadges(challenger).getCooldown(gym)))
				.display(Utils.parseItemId(Elgyms.menu.getCooldownMaterial()))
				.hideFlags(FlagType.All)
				.build();

		Button rules = GooeyButton.builder()
				.title(Elgyms.menu.getRulesTitle())
				.display(Utils.parseItemId(Elgyms.menu.getRulesButtonMaterial()))
				.hideFlags(FlagType.All)
				.lore(ElgymsUtils.getRulesLore(gym))
				.build();

		Button leaders = GooeyButton.builder()
				.title(Elgyms.menu.getLeaderTitle())
				.display(Utils.getPlayerHead(challenger))
				.hideFlags(FlagType.All)
				.onClick(e -> {
					UIManager.openUIForcefully(e.getPlayer(), new GymLeaders().getPage(gym));
				})
				.build();

		PlayerBadges badges = BadgeProvider.getBadges(challenger);

		Button dynamic;
		if (!GymProvider.getOpenGyms().contains(gym)) {
			dynamic = closed;
		} else if (gym.equals(GymProvider.getGymFromPlayer(challenger.getUuid()))) {
			dynamic = cancel;
		} else if (badges.getCooldown(gym) != null && badges.getCooldown(gym) > new Date().getTime()) {
			dynamic = cooldown;
		} else {
			dynamic = challenge;
		}


		ChestTemplate template = ChestTemplate.builder(3)
				.fill(Components.filler())
				.set(10, dynamic)
				.set(12, rules)
				.set(14, leaders)
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
