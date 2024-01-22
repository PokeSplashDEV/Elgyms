package org.pokesplash.elgyms.gym;

public class BannedPokemon {
	private String pokemon;
	private String form;
	private String ability;

	public BannedPokemon() {
		pokemon = "ninetales";
		form = "alola";
		ability = "snowwarning";
	}

	public String getPokemon() {
		return pokemon;
	}

	public String getForm() {
		return form;
	}

	public String getAbility() {
		return ability;
	}
}
