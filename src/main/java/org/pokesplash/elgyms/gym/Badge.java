package org.pokesplash.elgyms.gym;

import java.util.UUID;

/**
 * Stores data for the gym badge.
 */
public class Badge {
	private UUID id;
	private String name;
	private String material;

	public Badge() {
		name = "Cobble Badge";
		material = "cobblemon:leftovers";
		id = UUID.randomUUID();
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public UUID getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
