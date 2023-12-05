package org.pokesplash.elgyms.util;

import org.pokesplash.elgyms.config.Reward;
import org.pokesplash.elgyms.gym.GymConfig;
import org.pokesplash.elgyms.gym.Requirements;
import org.pokesplash.elgyms.type.Clause;

import java.util.ArrayList;

public abstract class ElgymsUtils {
	public static ArrayList<String> getRulesLore(GymConfig gym) {

		Requirements requirements = gym.getRequirements();

		ArrayList<String> rules = new ArrayList<>();

		rules.add("§6Level: §e" + requirements.getPokemonLevel());
		rules.add(requirements.isRaiseToCap() ? "§aRaise To Cap" : "§cNo Raise To Cap");
		rules.add("§3Pokemon Amount: §b" + requirements.getTeamSize());
		rules.add(requirements.isTeamPreview() ? "§aTeam Preview" : "§cNo Team Preview");
		rules.add("§5Clauses:");
		for (Clause clause : requirements.getClauses()) {
			rules.add("§d" + Utils.capitaliseFirst(clause.name()));
		}
		return rules;
	}
}
