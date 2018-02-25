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
package com.github.koshamo.fiddler.jfx;

import com.github.koshamo.fiddler.EventHandler;
import com.github.koshamo.fiddler.MessageBus;

import javafx.application.Application;

/**
 * As Java FX is a standalone framework for GUI based applications, it needs
 * some special code to integrate this framework with Fiddler or any other 
 * framework.
 * 
 *  Usually you would start the Java FX GUI with 
 *  <code>Application.launch()</code>. This static method call returns after
 *  the GUI has been closed, no further interaction from the calling code
 *  is possible. But to integrate a Java FX GUI as module with any other 
 *  modules using the Fiddler framework, a special handling for external
 *  interaction with the GUI framework is necessary.
 *  
 *  <code>FiddlerFxApp</code> extends the Java FX <code>Application</code>,
 *  so it may be started as a regular Java FX application. Additionally, 
 *  <code>FiddlerFxApp</code> implements <code>EventHandler</code>, so that
 *  it directly can be used as a handler to be registered in the
 *  <code>MessageBus</code>. As Java FXs application uses its own startup
 *  process, using an overloaded constructor doesn't seem to be an option to
 *  connect with the message bus, thus some special startup code is neccessary.
 *  
 *  It is best used with a thread using Fiddlers 
 *  <code>FiddlerFxAppRunner</code> that starts the application and setting
 *  the message bus connection afterwards.
 *  
 *  <pre>
 *  <code>
 *  MessageBus messageBus = new MessageBus();
 *  new Thread(new FiddlerFxAppRunner(MyFiddlerFxAppGui.class, args)).start();
 *  FiddlerFxApp.setMessageBus(messageBus);
 *  </code>
 *  </pre>
 * 
 * You are then able to connect to the message bus within your MyFiddlerFxAppGui
 * class and send messages to the message bus.
 * Note: you need to implement the methods <code>handle(Event event)</code>
 * and <code>shutdown()</code>, that come along with <code>EventHandler</code>
 * interface.
 * 
 * @author Dr. Jochen Raßler
 *
 */
public abstract class FiddlerFxApp extends Application implements EventHandler {

	private static MessageBus messageBus;

	/**
	 * Connect the Gui with the message bus
	 * @param messageBus	the message bus to connect to
	 */
	public static void setMessageBus(MessageBus messageBus) {
		FiddlerFxApp.messageBus = messageBus;
	}
	
	/**
	 * Get the message bus this Gui is connected to
	 * @return	the message bus connected to
	 */
	public static MessageBus getMessageBus() {
		return FiddlerFxApp.messageBus;
	}
}
