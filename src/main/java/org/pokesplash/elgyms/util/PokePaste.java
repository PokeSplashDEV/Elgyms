package org.pokesplash.elgyms.util;

import com.google.gson.JsonObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public abstract class PokePaste {
	public static ArrayList<JsonObject> parsePaste(String url) {
		HttpClient httpClient = HttpClient.newHttpClient();
		try {
			HttpRequest request = HttpRequest.newBuilder(URI.create(url))
							.GET().build();
			String response = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();

			String[] splits = response.replaceAll("<[^>]*>", "")
					.split("\n\n");

			ArrayList<JsonObject> pokemon = new ArrayList<>();

			// Ignores first and last of list, as they're not pokemon data.
			for (int x=0; x < splits.length - 1; x++) {
				if (!splits[x].trim().isBlank()) {
					System.out.println(splits[x].trim());
				}
			}



		} catch (Exception e) {
			return null;
		}
		return new ArrayList<>();
	}
}
