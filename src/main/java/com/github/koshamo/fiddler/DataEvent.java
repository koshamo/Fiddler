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
 * The DataEvent is the main Event for sending data all around a Fiddler
 * application. Use subclasses to specify your actual data events.
 * <p>
 * This abstract class uses generics to define its valueable usage:
 * <p>
 * <b>Meta data:</b> the meta data is used to describe the content of the data. 
 * In some applications enums may help to define the delivered data as unique
 * ID, in other applications using Strings may fit your needs 
 * (e.g. "Battery_Loadd"). You may also define your own class to define
 * complex meta data description, to be able to address the target to manage
 * the data in detail.
 * <p>
 * <b>Data:</b> the actual data, which may be of a simple type (you need classes, so
 * use the wrapper types for primitive data), user defined classes or any
 * collection.
 * <p>
 * Example:
 * <pre>
 * {@code
 * class BatteryLoadEvent extends DataEvent<String, Integer> {
 * 		BatteryLoadEvent (EventHandler source, EventHandler target, String meta, Integer data) {
 * 			super(source, target, meta, data);
 * 		}
 * }
 * 
 * messageBus.postEvent(new BatteryLoadEvent(this, null, "currentLoad", Integer.valueOf(17));
 * }
 * </pre>
 * 
 * @author Dr. Jochen Raßler
 *
 */
public abstract class DataEvent<M, T> extends Event {

	private final M meta;
	private final T data;
	
	/**
	 * The constructor must be called by any subclass. Source must be provided, 
	 * target may be null.
	 *  
	 * @param source	the sender of this event
	 * @param target	the target of this event, may be null
	 * @param meta		the meta data for this event
	 * @param data		the actual data for this event
	 */
	public DataEvent(EventHandler source, EventHandler target, M meta, T data) {
		super(source, target);
		this.meta = meta;
		this.data = data;
	}

	/**
	 * Get the meta data from this event
	 * @return	the meta data
	 */
	public M getMetaInformation() {
		return meta;
	}
	
	/**
	 * Get the data from this event
	 * @return	the data
	 */
	public T getData() {
		return data;
	}
}
