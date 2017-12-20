/*
 * Copyright [2017] [Dr. Jochen Raﬂler]
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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

/**
 * @author jochen
 *
 */
public class MessageBus {

	public static enum ListenerType {
		TARGET, ANY
	}
	
	private final class RegisteredHandler {
		private final EventHandler handler;
		private final ListenerType type;
		
		RegisteredHandler(EventHandler handler, ListenerType type) {
			this.handler = handler;
			this.type = type;
		}
		
		EventHandler getHandler() {
			return handler;
		}
		ListenerType getType() {
			return type;
		}
	}
	
	private final class EventRunner implements Runnable {

		boolean run = true;
		EventRunner() {
			// empty C'tor
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			while (run) {
				if (!eventQueue.isEmpty()) {
					Event ev = eventQueue.poll();
					// why does poll return a null element?
					// we checked the queue, if any item is there
					if (ev != null) {
						handleEvent(ev, eventHandlers);
						if (ev instanceof MessageEvent)
							handleEvent(ev, messageHandlers);
						if (ev instanceof RequestEvent)
							handleEvent(ev, requestHandlers);
						if (ev instanceof DataEvent)
							handleEvent(ev, dataHandlers);
					}

				} else {
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		
		private void handleEvent(Event ev, List<RegisteredHandler> list) {
			for (RegisteredHandler rh : list) {
				if (rh.getType() == ListenerType.ANY)
					rh.getHandler().handle(ev);
				if (rh.getType() == ListenerType.TARGET &&
						(ev.getTarget() == null
						|| ev.getTarget() == rh.getHandler()))
					rh.getHandler().handle(ev);
			}
		}

		void stopRunner() {
			run = false;
		}
	}
	
	List<RegisteredHandler> eventHandlers;
	List<RegisteredHandler> messageHandlers;
	List<RegisteredHandler> requestHandlers;
	List<RegisteredHandler> dataHandlers;
	Queue<Event> eventQueue;
	private EventRunner runner;
	
	public MessageBus() {
		eventHandlers = new ArrayList<>();
		messageHandlers = new ArrayList<>();
		requestHandlers = new ArrayList<>();
		dataHandlers = new ArrayList<>();
		eventQueue = new LinkedList<>();
		runner = new EventRunner();
		new Thread(runner).start();
	}
	
	public boolean postEvent(Event ev) {
		if (ev == null)
			return false;
		return eventQueue.offer(ev);
	}
	
	
	public void registerAllEvents(EventHandler handler, ListenerType type) {
		Objects.requireNonNull(handler, "You must register a non-null EventHandler");
		eventHandlers.add(new RegisteredHandler(handler, type));
	}
	
	public void unregisterAllEvents(EventHandler handler) {
		if (handler != null) 
			unregisterEvents(handler, eventHandlers);
	}

	public void registerMessageEvents(EventHandler handler, ListenerType type) {
		Objects.requireNonNull(handler, "You must register a non-null EventHandler");
		messageHandlers.add(new RegisteredHandler(handler, type));
	}
	
	public void unregisterMessageEvents(EventHandler handler) {
		if (handler != null) 
			unregisterEvents(handler, messageHandlers);
	}

	public void registerRequestEvents(EventHandler handler, ListenerType type) {
		Objects.requireNonNull(handler, "You must register a non-null EventHandler");
		requestHandlers.add(new RegisteredHandler(handler, type));
	}
	
	public void unregisterRequestEvents(EventHandler handler) {
		if (handler != null) 
			unregisterEvents(handler, requestHandlers);
	}

	public void registerDataEvents(EventHandler handler, ListenerType type) {
		Objects.requireNonNull(handler, "You must register a non-null EventHandler");
		dataHandlers.add(new RegisteredHandler(handler, type));
	}
	
	public void unregisterDataEvents(EventHandler handler) {
		if (handler != null) 
			unregisterEvents(handler, dataHandlers);
	}

	private static void unregisterEvents(EventHandler handler, List<RegisteredHandler> list) {
		list.parallelStream()
		.filter(rh -> rh.getHandler() == handler)
		.forEach(list::remove);
		
	}
} 
