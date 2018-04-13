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
 * The RequestEvent is used to request a set of data. The meta data object
 * describes the requested data.
 * <p>
 * In some applications modules just send their data, as soon as it is availabe.
 * In most application, e.g. GUI-based applications, data has to be requested,
 * when some events occur: a user pushes a button and wants to list some data.
 * <p>
 * To get the data, you need to request the data from a module. Here comes the
 * RequestEvent to request this data. As this class is abstract, you need to
 * create your own sublass extending this RequestEvent and provide a
 * concrete Type for the meta data.
 * <p>
 * <b>Meta data:</b> the meta data is used to describe the content of the data
 * requested. In some applications enums may help to define the requested
 * data as unique ID, in other applications using Strings may fit your needs 
 * (e.g. "Battery_Loadd"). You may also define your own class to define
 * complex meta data description, to be able to address the target to manage
 * the data in detail.
 * <p>
 * Example:
 * <pre>
 * {@code
 * class BatteryLoadRequestEvent extends DataEvent<String> {
 * 	BatteryLoadRequestEvent (EventHandler source, EventHandler target, String meta) {
 * 		super(source, target, meta);
 * 	}
 * }
 * 
 * messageBus.postEvent(new BatteryLoadRequestEvent(this, null, "getCurrentLoad");
 * }
 * </pre>
 * The module communicating with the hardware would now listen to this event
 * and do, what it needs to do to provide the current battery load.
 * As soon as it has the current battery load available, the module would now
 * send a data event to provide the requested data.
 * <p>
 * It's up to you, if you use the same meta data for request and data events,
 * or to have different meta types. But it is nice style to use the same meta
 * data, so the requesting module can now check with <code>==</code> or the 
 * equals method.
 *  
 * @see DataEvent

 * @author Dr. Jochen Raßler
 *
 */
public abstract class RequestEvent<M> extends Event {

	private final M meta;
	
	/**
	 * The constructor needs the message sender and optionally the message target.
	 * Additionally the meta data object is used to request a set of data
	 * from all listening modules. Normally only one module would feel guilty
	 * and provide the data.
	 * 
	 * @param source	the sender of this message
	 * @param target	the target of this message, may be null
	 * @param meta		the meta data
	 */
	public RequestEvent(EventHandler source, EventHandler target, M meta) {
		super(source, target);
		this.meta = meta;
	}
	
	/**
	 * Get the meta data from this event
	 * @return	the meta data
	 */
	public M getMetaInformation() {
		return meta;
	}
	
}
