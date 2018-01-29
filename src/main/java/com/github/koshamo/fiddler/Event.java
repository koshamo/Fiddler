/*
 * Copyright [2017] [Dr. Jochen Raßler]
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.koshamo.fiddler;

import java.util.Objects;

/**
 * The parent class of all Fiddler events.
 * <p>
 * Normally you would use one of the specialized Events for the application.
 * But you may create your own Event class inheriting from this class, as
 * all Events need to be subclasses of Event to be managed by the message bus.
 * 
 * @author Dr. Jochen Raßler
 *
 */
public abstract class Event {
	private final EventHandler source;
	private final EventHandler target;

	/**
	 * The basic constructor uses only source and target of events.
	 * 
	 * @param source	the sender of this event
	 * @param target	the target of this event, may be null
	 */
	public Event(EventHandler source, EventHandler target) {
		Objects.requireNonNull(source, "You must specify a EventHandler source");
		this.source = source;
		this.target = target;
	}
	/**
	 * get the sender of this message
	 * @return	the sender
	 */
	public EventHandler getSource() {
		return source;
	}

	/**
	 * get the target of this event. May return null!
	 * @return	the target
	 */
	public EventHandler getTarget() {
		return target;
	}
}
