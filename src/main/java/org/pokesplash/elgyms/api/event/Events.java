package org.pokesplash.elgyms.api.event;

import org.pokesplash.elgyms.api.event.events.ExampleEvent;
import org.pokesplash.elgyms.api.event.obj.Event;

/**
 * Class that holds all of the events.
 */
public abstract class Events {
	public static Event<ExampleEvent> EXAMPLE = new Event<>();

}