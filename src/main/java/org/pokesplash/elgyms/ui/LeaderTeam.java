package org.pokesplash.elgyms.ui;

import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.button.FlagType;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.button.PlaceholderButton;
import ca.landonjw.gooeylibs2.api.helpers.PaginationHelper;
import ca.landonjw.gooeylibs2.api.page.LinkedPage;
import ca.landonjw.gooeylibs2.api.page.Page;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import com.cobblemon.mod.common.CobblemonItems;
import com.cobblemon.mod.common.item.CobblemonItem;
import com.cobblemon.mod.common.item.PokemonItem;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.pokesplash.elgyms.gym.GymConfig;
import org.pokesplash.elgyms.util.ElgymsUtils;

import java.util.ArrayList;

public class LeaderTeam {
    public Page getPage(ServerPlayerEntity leader, GymConfig gym) {
        ArrayList<Button> pokemon = new ArrayList<>();
        for (JsonObject object : gym.getLeader(leader.getUuid()).getTeam()) {
            Pokemon pokemonObject = new Pokemon().loadFromJSON(object).initialize();

            pokemon.add(GooeyButton.builder()
                    .title(pokemonObject.getDisplayName().getString())
                    .display(PokemonItem.from(pokemonObject))
                    .lore(Text.class, ElgymsUtils.parse(pokemonObject))
                    .build()
            );
        }

        if (pokemon.size() > 4) {
            pokemon.add(3, GooeyButton.builder()
                    .title("")
                    .display(new ItemStack(CobblemonItems.POKE_BALL))
                    .hideFlags(FlagType.All)
                    .build());
        }

        ChestTemplate template = ChestTemplate.builder(3)
                .rectangle(1, 1, 1, 7, new PlaceholderButton())
                .fill(Components.filler())
                .build();

        LinkedPage page = PaginationHelper.createPagesFromPlaceholders(template, pokemon, null);
        page.setTitle(gym.getName() + " - " + leader.getName().getString());

        return page;
    }
}
