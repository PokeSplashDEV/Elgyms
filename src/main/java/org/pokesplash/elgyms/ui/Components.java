package org.pokesplash.elgyms.ui;

import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.button.ButtonAction;
import ca.landonjw.gooeylibs2.api.button.FlagType;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import org.pokesplash.elgyms.Elgyms;
import org.pokesplash.elgyms.util.Utils;

import java.util.function.Consumer;

public abstract class Components {
	public static Button backButton(Consumer<ButtonAction> callback) {
		return GooeyButton.builder()
				.title(Elgyms.menu.getBackButton())
				.display(Utils.parseItemId(Elgyms.menu.getBackButtonMaterial()))
				.hideFlags(FlagType.All)
				.onClick(callback)
				.build();
	}

	public static Button filler() {
		return GooeyButton.builder()
				.title("")
				.display(Utils.parseItemId(Elgyms.menu.getFillerMaterial()))
				.hideFlags(FlagType.All)
				.build();
	}
}
