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
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.badge.PlayerBadges;
import org.pokesplash.elgyms.champion.ChampionConfig;
import org.pokesplash.elgyms.config.CategoryConfig;
import org.pokesplash.elgyms.gym.GymConfig;
import org.pokesplash.elgyms.gym.Leader;
import org.pokesplash.elgyms.provider.BadgeProvider;
import org.pokesplash.elgyms.provider.GymProvider;
import org.pokesplash.elgyms.util.ElgymsUtils;
import org.pokesplash.elgyms.util.Utils;

import java.util.ArrayList;
import java.util.Date;

public class ChampionInfo {
	public Page getPage(ServerPlayerEntity challenger) {

		ChampionConfig config = GymProvider.getChampion();

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
						ElgymsUtils.checkChampionRequirements(challenger, pokemons);

						GymProvider.challengeChampion(e.getPlayer());
					} catch (Exception ex) {
						e.getPlayer().sendMessage(Text.literal("§c" + ex.getMessage()));
					}

					UIManager.openUIForcefully(e.getPlayer(), new ChampionInfo().getPage(challenger));
				})
				.build();

		Button cancel = GooeyButton.builder()
				.title(Elgyms.menu.getCancelChallengeButtonTitle())
				.display(Utils.parseItemId(Elgyms.menu.getCancelChallengeButtonMaterial()))
				.hideFlags(FlagType.All)
				.onClick(e -> {
					GymProvider.cancelChampionChallege(e.getPlayer().getUuid());

					e.getPlayer().sendMessage(Text.literal(Elgyms.lang.getPrefix() +
							"§3You have canceled your Champion challenge."));

					UIManager.openUIForcefully(e.getPlayer(), new ChampionInfo().getPage(challenger));
				})
				.build();

		Button closed = GooeyButton.builder()
				.title("§cThe Champion is currently unavailable.")
				.display(Utils.parseItemId(Elgyms.menu.getClosedButtonMaterial()))
				.hideFlags(FlagType.All)
				.build();

		ArrayList<String> rulesLore = new ArrayList<>();

		Button rules = GooeyButton.builder()
				.title(Elgyms.menu.getRulesTitle())
				.display(Utils.parseItemId(Elgyms.menu.getRulesButtonMaterial()))
				.hideFlags(FlagType.All)
				.lore(rulesLore)
				.build();

		Button leaders = GooeyButton.builder()
				.title("§3Previous Champions")
				.display(Utils.getPlayerHead(challenger))
				.hideFlags(FlagType.All)
				.onClick(e -> {
					UIManager.openUIForcefully(e.getPlayer(), new PreviousChampions().getPage());
				})
				.build();

		PlayerBadges badges = BadgeProvider.getBadges(challenger);

		Button needsBadge = GooeyButton.builder()
				.title("§cYou require the " + GymProvider.getGymFromBadge(config.getRequiredBadge()) +
						" to challenge the Champion.")
				.display(Utils.parseItemId(Elgyms.menu.getClosedButtonMaterial()))
				.hideFlags(FlagType.All)
				.build();

		Leader champion = config.getChampion();

		Button dynamic;
		if (champion == null || Elgyms.server.getPlayerManager().getPlayer(champion.getUuid()) == null) {
			dynamic = closed;
		} else if (GymProvider.getChampQueue().isInQueue(challenger.getUuid())) {
			dynamic = cancel;
		} else if (!badges.containsBadge(config.getRequiredBadge())) {
			dynamic = needsBadge;
		} else {
			dynamic = challenge;
		}


		ChestTemplate template = ChestTemplate.builder(3)
				.fill(Components.filler())
				.set(10, dynamic)
				.set(12, rules)
				.set(14, leaders)
				.set(16, Components.backButton(e -> {
					UIManager.openUIForcefully(e.getPlayer(), new CategorySelect().getPage());
				}))
				.build();

		return GooeyPage.builder()
				.template(template)
				.title("Champion")
				.build();
	}
}
