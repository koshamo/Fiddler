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
 * A module that wants to register to the message bus to receive messages
 * or notifications needs to implement this interface.
 * <p>
 * As soon as any module sends a message to the message bus, it will be
 * distributed to all <code>EventHandler</code>s registered to the message bus,
 * depending on the registration policy. The distribution mechanism calls
 * the method <code>handle(Event event)</code>, which then can implement the 
 * reaction to this message.
 * <p>
 * Example:
 * <pre>
 * <code>
 * public class StrBoolDataEvent extends {@code DataEvent<String, Boolean>} {
 * 		public StrBoolDataEvent(EventHandler source, EventHandler target, String meta, Boolean data) {
 * 			super(source, target, meta, data);
 * 		}
 * }
 *
 * public MyClass implements EventHandler {
 * 		{@literal @}Override
 * 		public void handle(Event event) {
 *			if (event instanceof StrBoolDataEvent) {
 * 				StrBoolDataEvent de = (StrBoolDataEvent) event;
 * 				if (de.getMetaInformation().equals("KEY_ONE"))
 * 					doActionOne();
 *				if (de.getMetaInformation().equals("KEY_TWO"))
 *					doActionTwo();
 *			}
 * 		}
 * }
 * </code>
 * </pre>
 * 
 * If any module sends an <code>ExitEvent</code> to the message bus, the
 * message bus calls the method <code>shutdown()</code> on every registered
 * module. Thus every module must implement the <code>shutdown()</code> method
 * and may implement some special code to safely shut this module down, e.g.
 * save data, stop threads etc.
 *  
 * @author Dr. Jochen Raßler
 *
 */
public interface EventHandler{
	
	/**
	 * this method handles incoming events from the message bus.
	 * <p>
	 * choose whatever messages may be of interest for you and implement
	 * how to react to this message.
	 * @param event	the event that is distributed by the message bus
	 */
	void handle(Event event);
	
	/**
	 * this method is called, as soon as any module sends an 
	 * <code>ExitEvent</code> to the message bus.
	 * <p>
	 * This method is something like a destructor for your current module,
	 * so you may write some clean up code to safely shut your module down
	 * and do what needs to be done before the module exits. 
	 */
	void shutdown();
}
