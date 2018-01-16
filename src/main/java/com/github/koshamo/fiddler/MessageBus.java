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

import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

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
		boolean exitSignal = false;
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
					deleteNulls();
					Event ev = eventQueue.poll();
					// why does poll return a null element?
					// we checked the queue, if any item is there
					if (ev != null) {
						if (ev instanceof ExitEvent) {
							shutdown();
							exitSignal = true;
							continue;
						}
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
				removeUnregisteredHandlers();
				if (exitSignal) {
					if (eventHandlers.isEmpty() 
							&& messageHandlers.isEmpty() 
							&& requestHandlers.isEmpty() 
							&& dataHandlers.isEmpty())
						stopRunner();
				}
			}
			System.exit(0);
		}
		
		private void removeUnregisteredHandlers() {
			eventHandlers.removeAll(eventHandlersToBeRemoved);
			eventHandlersToBeRemoved.clear();
			messageHandlers.removeAll(messageHandlersToBeRemoved);
			messageHandlersToBeRemoved.clear();
			requestHandlers.removeAll(requestHandlersToBeRemoved);
			requestHandlersToBeRemoved.clear();
			dataHandlers.removeAll(dataHandlersToBeRemoved);
			dataHandlersToBeRemoved.clear();
		}
		
		private void deleteNulls() {
			deleteNulls(eventHandlers);
			deleteNulls(messageHandlers);
			deleteNulls(requestHandlers);
			deleteNulls(dataHandlers);
		}
		
		private void deleteNulls(List<RegisteredHandler> list) {
			while (list.contains(null)) 
				list.remove(list.indexOf(null));
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

		private void shutdown() {
			eventHandlers.stream().forEach(e -> e.getHandler().shutdown());
			messageHandlers.stream().forEach(e -> e.getHandler().shutdown());
			requestHandlers.stream().forEach(e -> e.getHandler().shutdown());
			dataHandlers.stream().forEach(e -> e.getHandler().shutdown());
		}
		
		void stopRunner() {
			run = false;
		}
	}
	
	List<RegisteredHandler> eventHandlers;
	List<RegisteredHandler> messageHandlers;
	List<RegisteredHandler> requestHandlers;
	List<RegisteredHandler> dataHandlers;
	List<RegisteredHandler> eventHandlersToBeRemoved;
	List<RegisteredHandler> messageHandlersToBeRemoved;
	List<RegisteredHandler> requestHandlersToBeRemoved;
	List<RegisteredHandler> dataHandlersToBeRemoved;
	Queue<Event> eventQueue;
	private EventRunner runner;
	
	public MessageBus() {
		eventHandlers = new Vector<>();
		messageHandlers = new Vector<>();
		requestHandlers = new Vector<>();
		dataHandlers = new Vector<>();
		eventHandlersToBeRemoved = new Vector<>();
		messageHandlersToBeRemoved = new Vector<>();
		requestHandlersToBeRemoved = new Vector<>();
		dataHandlersToBeRemoved = new Vector<>();
		eventQueue = new ConcurrentLinkedQueue<>();
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
		Objects.requireNonNull(type, "You must specify a ListenerType");
		eventHandlers.add(new RegisteredHandler(handler, type));
	}
	
	public void unregisterAllEvents(EventHandler handler) {
		if (handler != null) 
			unregisterEvents(handler, eventHandlers, eventHandlersToBeRemoved);
	}

	public void registerMessageEvents(EventHandler handler, ListenerType type) {
		Objects.requireNonNull(handler, "You must register a non-null EventHandler");
		Objects.requireNonNull(type, "You must specify a ListenerType");
		messageHandlers.add(new RegisteredHandler(handler, type));
	}
	
	public void unregisterMessageEvents(EventHandler handler) {
		if (handler != null) 
			unregisterEvents(handler, messageHandlers, messageHandlersToBeRemoved);
	}

	public void registerRequestEvents(EventHandler handler, ListenerType type) {
		Objects.requireNonNull(handler, "You must register a non-null EventHandler");
		Objects.requireNonNull(type, "You must specify a ListenerType");
		requestHandlers.add(new RegisteredHandler(handler, type));
	}
	
	public void unregisterRequestEvents(EventHandler handler) {
		if (handler != null) 
			unregisterEvents(handler, requestHandlers, requestHandlersToBeRemoved);
	}

	public void registerDataEvents(EventHandler handler, ListenerType type) {
		Objects.requireNonNull(handler, "You must register a non-null EventHandler");
		Objects.requireNonNull(type, "You must specify a ListenerType");
		dataHandlers.add(new RegisteredHandler(handler, type));
	}
	
	public void unregisterDataEvents(EventHandler handler) {
		if (handler != null) 
			unregisterEvents(handler, dataHandlers, dataHandlersToBeRemoved);
	}

	private static void unregisterEvents(EventHandler handler, 
			List<RegisteredHandler> list, List<RegisteredHandler> listToBeRemoved) {
//		list.parallelStream()
//		.filter(rh -> rh.getHandler() == handler)
//		.forEach(list::remove);
		list.stream()
		.filter(rh -> rh.getHandler() == handler)
		.forEach(listToBeRemoved::add);
	}
	
} 
