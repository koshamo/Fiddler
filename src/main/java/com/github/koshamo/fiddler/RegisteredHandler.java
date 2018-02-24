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

import com.github.koshamo.fiddler.MessageBus.ListenerType;

/**
 * This class is a helper class for the MessageBus class to store the 
 * ListenerType to every registered handler.
 * 
 * @author Dr. Jochen Raßler
 *
 */
// TODO: can this class be replaced by using a Map?
final class RegisteredHandler {
	private final EventHandler handler;
	private final ListenerType type;
	
	/**
	 * Create the registered handler using the ListenerType
	 * @param handler	the handler to register
	 * @param type		the ListenerType of this registered handler
	 */
	RegisteredHandler(EventHandler handler, ListenerType type) {
		this.handler = handler;
		this.type = type;
	}
	
	/**
	 * get the EventHandler
	 * @return	the registered handler
	 */
	EventHandler getHandler() {
		return handler;
	}
	
	/**
	 * get the ListenerType of this registered handler
	 * @return	the listener type
	 */
	ListenerType getType() {
		return type;
	}
}
