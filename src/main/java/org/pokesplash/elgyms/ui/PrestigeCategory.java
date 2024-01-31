package org.pokesplash.elgyms.ui;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.button.FlagType;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.page.GooeyPage;
import ca.landonjw.gooeylibs2.api.page.Page;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.badge.PlayerBadges;
import org.pokesplash.elgyms.config.CategoryConfig;
import org.pokesplash.elgyms.config.Reward;
import org.pokesplash.elgyms.gym.Badge;
import org.pokesplash.elgyms.gym.GymConfig;
import org.pokesplash.elgyms.provider.BadgeProvider;
import org.pokesplash.elgyms.provider.GymProvider;
import org.pokesplash.elgyms.util.ElgymsUtils;
import org.pokesplash.elgyms.util.Utils;

import java.awt.*;
import java.util.ArrayList;

public class PrestigeCategory {
    public Page getPage(ServerPlayerEntity player, CategoryConfig category) {

        PlayerBadges badges = BadgeProvider.getBadges(player);

        GooeyButton prestige;

        // If the player has the required badge, offer prestige.
        if (badges.containsBadge(category.getPrestige().getRequiredBadge())) {
            ArrayList<String> lore = new ArrayList<>();
            lore.add("§2You can prestige and remove all of your badges for " + category.getName());

            prestige = GooeyButton.builder()
                    .title("§2Prestige")
                    .display(Utils.parseItemId(Elgyms.menu.getChallengeButtonMaterial()))
                    .hideFlags(FlagType.All)
                    .lore(lore)
                    .onClick(e -> {
                        ArrayList<Badge> categoryBadges = badges.getBadgeIDs(category);

                        // Removes the badges for the category.
                        badges.removeBadgesForCategory(category);

                        // Sets prestige to true.
                        badges.setPrestiged(category, true);

                        // Get the players rewards
                        Reward reward = category.getPrestige().getRewards();

                        // Broadcasts the message
                        if (reward.isEnableBroadcast()) {
                            Utils.broadcastMessage(Utils.formatPlaceholders(reward.getBroadcastMessage(), null,
                                    null, e.getPlayer().getName().getString(), category, null, null));
                        }

                        // Run commands
                        Utils.runCommands(reward.getCommands(), e.getPlayer().getName().getString(),
                                null, category, null);

                        UIManager.closeUI(e.getPlayer());
                    })
                    .build();
        } else { // Otherwise say they don't have the required badges.

            GymConfig gym = GymProvider.getGymFromBadge(category.getPrestige().getRequiredBadge());

            // If the gym couldn't be found, display an error.
            if (gym == null) {
                prestige = GooeyButton.builder()
                        .title("§cCould not find the required Badge")
                        .display(Utils.parseItemId(Elgyms.menu.getCancelChallengeButtonMaterial()))
                        .hideFlags(FlagType.All)
                        .build();
            } else { // Otherwise display the button with the required badge.
                ArrayList<String> lore = new ArrayList<>();
                lore.add("§cYou require the " + gym.getBadge().getName() + "§c Badge to prestige this category");

                prestige = GooeyButton.builder()
                        .title("§cUnable To Prestige")
                        .display(Utils.parseItemId(Elgyms.menu.getClosedButtonMaterial()))
                        .hideFlags(FlagType.All)
                        .lore(lore)
                        .build();
            }
        }

        Button back = Components.backButton(e -> {
            UIManager.openUIForcefully(player, new PrestigeCategorySelect().getPage());
        });

        ChestTemplate template = ChestTemplate.builder(3)
                .fill(Components.filler())
                .set(11, prestige)
                .set(15, back)
                .build();

        return GooeyPage.builder()
                .template(template)
                .title("Confirm Prestige")
                .build();
    }
}
