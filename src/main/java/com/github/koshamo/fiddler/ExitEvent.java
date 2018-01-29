/*
 * Copyright [2018] [Dr. Jochen Raßler]
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

/**
 * The exit event is a event, that is distributed to all modules registered
 * to the message bus and calls their shutdown method. This event triggers
 * a regular application shutdown process and all modules have the ability
 * to do what they need to do before application exit, e.g. stop threads, 
 * writing data etc.
 * 
 * @author Dr. Jochen Raßler
 *
 */
public class ExitEvent extends Event {

	/**
	 * The constructor of the exit event. Source must be provided, target may be 
	 * null.
	 * 
	 * @param source	the sender of this event
	 * @param target	the target of this event, may be null
	 */
	public ExitEvent(EventHandler source, EventHandler target) {
		super(source, target);
	}

}
