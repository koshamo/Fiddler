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

/**
 * The message event is a simple event, that can be used to distribute
 * simple information along the Fiddler based application.
 * <p>
 * In some applications it is helpful to distribute simple information 
 * to all modules. This message event is designed for it.
 * <p>
 * Example:
 * <pre>
 * {@code
 * messageBus.postEvent(new MessageEvent(this, null, "settings available");
 * }
 * </pre>
 * @author Dr. Jochen Raßler
 *
 */
public abstract class MessageEvent extends Event {
	
	private final String message;
	
	/**
	 * The constructor needs the message sender and optionally the message target.
	 * Additionally a simple String message object is used to distribute
	 * the message to all listening modules.
	 * 
	 * @param source	the sender of this message
	 * @param target	the target of this message, may be null
	 * @param message	the message to distribute
	 */
	public MessageEvent(EventHandler source, EventHandler target, String message) {
		super(source, target);
		this.message = message;
	}

	/**
	 * get the message from this event
	 * @return	the message as String
	 */
	public String getMessage() {
		return message;
	}

}
