package org.pokesplash.elgyms.event;

import org.pokesplash.elgyms.event.events.ExampleEvent;
import org.pokesplash.elgyms.event.obj.Event;

/**
 * Class that holds all of the events.
 */
public abstract class Events {
	public static Event<ExampleEvent> EXAMPLE = new Event<>();

}