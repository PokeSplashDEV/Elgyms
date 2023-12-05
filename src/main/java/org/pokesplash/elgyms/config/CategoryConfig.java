package org.pokesplash.elgyms.config;

import java.util.ArrayList;

/**
 * Different Categories of gyms.
 */
public class CategoryConfig {
	private String name; // The category name (eg. Elite 4)
	private int displaySlot; // The slot in the UI to display the category in;
	private String displayItem; // The item to display for the category button.
	private PrestigeConfig prestige; // Prestige settings for the category;

	public CategoryConfig() {
		this.name = "Normal";
		this.displaySlot = 11;
		this.displayItem = "cobblemon:poke_ball";
		prestige = new PrestigeConfig();

	}

	/**
	 * Getters for each field.
	 */

	public String getName() {
		return name;
	}

	public int getDisplaySlot() {
		return displaySlot;
	}

	public String getDisplayItem() {
		return displayItem;
	}

	public PrestigeConfig getPrestige() {
		return prestige;
	}
}
