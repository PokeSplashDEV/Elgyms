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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.badge.PlayerBadges;
import org.pokesplash.elgyms.config.CategoryConfig;
import org.pokesplash.elgyms.exception.GymException;
import org.pokesplash.elgyms.gym.Badge;
import org.pokesplash.elgyms.gym.GymConfig;
import org.pokesplash.elgyms.provider.GymProvider;
import org.pokesplash.elgyms.util.Utils;
import org.pokesplash.teampreview.TeamPreview;

import java.util.ArrayList;
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
									// If its team preview, open the preview window, else just start the battle.
									if (gym.getRequirements().isTeamPreview()) {
										try {
											GymProvider.giveLeaderPokemon(leader, gym);
											TeamPreview.createPreview(leader.getUuid(), challenger.getUuid(), f -> {
												try {
													GymProvider.beginBattle(challenger, leader, gym, false);
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
										GymProvider.beginBattle(challenger, leader, gym, true);
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
