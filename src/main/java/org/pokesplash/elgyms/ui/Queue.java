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
import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.exception.GymException;
import org.pokesplash.elgyms.gym.GymConfig;
import org.pokesplash.elgyms.provider.BattleProvider;
import org.pokesplash.elgyms.provider.GymProvider;
import org.pokesplash.elgyms.util.ElgymsUtils;
import org.pokesplash.elgyms.util.Utils;
import org.pokesplash.teampreview.TeamPreview;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Queue {
	public Page getPage(ServerPlayerEntity leader) {

		ArrayList<ServerPlayerEntity> leaderQueues = new ArrayList<>();
		for ( GymConfig gym : GymProvider.getGymsByLeader(leader.getUuid())) {
			for (UUID challenger : GymProvider.getQueueFromGym(gym).getQueue()) {
				leaderQueues.add(Elgyms.server.getPlayerManager().getPlayer(challenger));
			}
		}

		ArrayList<Button> buttons = new ArrayList<>();
		for (ServerPlayerEntity challenger : leaderQueues) {
			GymConfig gym = GymProvider.getGymFromPlayer(challenger.getUuid());
			buttons.add(
					GooeyButton.builder()
							.title(challenger.getName().getString())
							.display(Utils.getPlayerHead(challenger))
							.hideFlags(FlagType.All)
							.onClick(e -> {
								UIManager.closeUI(leader);
								try {

									if (challenger.getUuid().equals(leader.getUuid())) {
										e.getPlayer().sendMessage(Text.literal(Elgyms.lang.getPrefix() +
												"§cYou can not battle yourself."));
										return;
									}

									// If its team preview, open the preview window, else just start the battle.
									if (gym.getRequirements().isTeamPreview()) {
										try {
											ArrayList<Pokemon> leaderTeam = BattleProvider.getLeaderTeam(leader, gym);
											TeamPreview.createPreview(leader.getUuid(), challenger.getUuid(),
													leaderTeam, BattleProvider.toList(Cobblemon.INSTANCE.getStorage().getParty(challenger)),
													f -> {
														try {
															// Moves the Pokemon at the lead index to the beginning of the list.
															List<Pokemon> leaderPokemon = f.getPlayer1().getPokemon();
															Pokemon leaderLead = leaderPokemon.get(f.getPlayer1().getLead());
															leaderPokemon.remove(f.getPlayer1().getLead());
															leaderPokemon.add(0, leaderLead);

															// Moves the Pokemon at the lead index to the beginning of the list.
															List<Pokemon> challengerPokemon = f.getPlayer2().getPokemon();
															Pokemon challengerLead = challengerPokemon.get(f.getPlayer2().getLead());
															challengerPokemon.remove(f.getPlayer2().getLead());
															challengerPokemon.add(0, challengerLead);

															BattleProvider.beginBattle(challenger, leader, gym,
																	ElgymsUtils.toJson(leaderPokemon), ElgymsUtils.toJson(challengerPokemon));
														} catch (Exception ex) {
															// Sends error to leader. Tells challenger something went wrong.
															leader.sendMessage(Text.literal("§c" + ex.getMessage()));
															challenger.sendMessage(Text.literal("§c" + "Something went wrong, the leader has more info."));

															if (!(ex instanceof GymException)) {
																Elgyms.LOGGER.error(ex.getMessage());
															}
														}
													});
											TeamPreview.openPreview(leader.getUuid());
											TeamPreview.openPreview(challenger.getUuid());
										} catch (Exception ex) {
											ex.printStackTrace();
										}
									} else {
										BattleProvider.beginBattle(challenger, leader, gym);
									}
								} catch (Exception ex) {
									e.getPlayer().sendMessage(Text.literal("§c" + ex.getMessage()));
								}
							})
							.build()
			);
		}

		int rows = buttons.isEmpty() ? 3 : (int) Math.ceil((double) buttons.size() / 7) + 2;

		ChestTemplate template = ChestTemplate.builder(rows)
				.rectangle(1, 1, rows - 2, 7, new PlaceholderButton())
				.fill(Components.filler())
				.build();

		LinkedPage page = PaginationHelper.createPagesFromPlaceholders(template, buttons, null);
		page.setTitle("Queue");

		return page;
	}
}
