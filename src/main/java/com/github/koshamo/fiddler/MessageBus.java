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
 * The MessageBus class is the main class for a message based modularized
 * application. It is intended to distribute messages between modules to
 * support loose coupling of modules to to eliminate the dependencies
 * between the modules.
 * 
 * The message bus is best used in startup code (e.g. main() method). After
 * creating a message bus object you then can instantiate the modules, which 
 * need to implement the <code>EventHandler</code> interface and need the 
 * message bus as an argument. This is needed to register the modules to
 * the message bus.
 * <pre>
 * <code>
 * public static void main(String[] args) {
 * 	MessageBus messageBus = new MessageBus();
 * 	new Thread(MyRunnableModuleOne(messageBus).start();
 * 	new Thread(MyRunnableModuleTwo(messageBus).start();
 * 	new MyNotRunnableModuleThree(messageBus);
 * }
 * </code>
 * </pre> 
 * The modules now know the message bus and can register to receive events.
 * For registering there exist two options: listen to all events or listen
 * for targeted objects.
 * If an event is send without a target, it is distributed to every
 * registered modules and that modules <code>handle(Event event)</code>
 * method is invoked.<br>
 * If a EventHandler (which is a module) registers with 
 * <code>ListenerType.ANY</code> it receives every message, too. It doesn't 
 * matter if the target is null or any specific target. This option is
 * for logging modules as well as for modules replacing any legacy modules.<br>
 * If a EventHandler registers with <code>ListenerType.TARGET</code>, it then
 * receives only messages, if the target is null or the module itself. This 
 * option is used, if there exist many modules and you don't want to much
 * selection code for specific messages just to increase performance.
 * 
 * All events are subtypes of <code>Event</code>. Currently there exist some
 * basic events, that help you implement your application.
 * <ul>
 * <li><code>MessageEvent</code> is a simple String-based event, that helps
 * you to distribute status message or other simple information through your
 * software system.
 * <li><code>RequestEvent</code> is an abstract event and meant to be subtyped
 * to send requests through the application. The RequestEvent contains 
 * meta data describing the action needed to be performed or data to get
 * delivered.
 * <li><code>DataEvent</code> is the best answer to the RequestEvent: the 
 * abstract DataEvent holds to generic types, one for the meta data and one for
 * the application data. Subtype this class with concrete types and send
 * your data, which is the application data. This data is described using the
 * meta data type. If the DataEvent is used as an answer to a RequestEvent,
 * the meta data of the RequestEvent should be used for the DataEvent and the
 * requesters source should be used as the target of the DataEvent to help
 * other modules to reduce the incoming messages, which increases performance.
 * <li><code>ExitEvent</code> is a regular message event, too, but is treated 
 * totally different by the message bus. All message types above, including 
 * the <code>Event</code> cause the message bus to call the 
 * <code>EventHandler</code>s <code>handle(Event event)</code> method.
 * The <code>ExitEvent</code> is used to send every single module, regardless
 * of their registered <code>ListenerType</code> an exit event, which means
 * the <code>shutdown()</code> method is invoked. Thus every modules has the
 * chance to do what needs to be done before system shutdown. It may send
 * an ExitEvent by themselves after shutdown has been accomplished to tell
 * it is ready to go down. 
 * </ul>
 * 
 * The <code>EventHandler</code>s or modules may register to every event type
 * (<code>registerAllEvents(EventHandler, ListenerType)</code>) or to specific 
 * events only, using the methods 
 * <code>registerMessageEvents(EventHandler, ListenerType)</code>,
 * <code>registerRequestEvents(EventHandler, ListenerType)</code>, and
 * <code>registerDataEvents(EventHandler, ListenerType)</code> respectively. 
 * The handlers may register to different events, for example to message events
 * and to data events.
 * 
 * For every register method exists a conforming unregister method to unregister
 * from this type of events. 
 * 
 * It is also possible to register to all events and to message events (or
 * request or data events) and then to unregister from all events. The handler
 * then still is registered to message events in this case! If you are 
 * registered to all events and, as above, to message events, you will receive
 * the message event twice!
 * 
 * @author Dr. Jochen Raßler
 *
 */
public class MessageBus {

	/**
	 * The ListenerType is used to register EventHandlers to the MessageBus.
	 * If no target is specified (== null), every message will be distributed to every
	 * EventHandler (module). If a target is specified, 
	 * <code>ListenerType.TARGET</code> handlers will only receive this message,
	 * if the target and the EventHandler are the same. If the handler is 
	 * registered with <code>ListenerType.ANY</code>, it receives every sent
	 * message. This can be helpful to replace legacy modules, to implement
	 * logging modules and the like. 
	 * 
	 * @author Dr. Jochen Raßler
	 *
	 */
	public static enum ListenerType {
		/**
		 * if the message target is not null, this handler only receives
		 * the message, if the handler is the target
		 */
		TARGET, 
		/**
		 * the handler always receives the message, regardless of the target
		 */
		ANY
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
	
	/**
	 * Create a MessageBus and start it.
	 * 
	 * Life can be that easy!
	 */
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
	
	/**
	 * Call this method within your module to distribute your <code>Event</code>
	 * to all other (or listening) modules (which are <code>EventHandler</code>s).
	 * 
	 * @param ev	the event to be distributed
	 * @return		true, if the event can be processed, false otherwise, which 
	 * may occur, when the event queue is full or the event had been null
	 */
	public boolean postEvent(Event ev) {
		if (ev == null)
			return false;
		return eventQueue.offer(ev);
	}
	
	
	/**
	 * register your EventHandler to listen for every event type, that is send 
	 * through the message bus.
	 * 
	 * @param handler	your event handler. Most used with <b>this</b>
	 * @param type		the listener type
	 */
	public void registerAllEvents(EventHandler handler, ListenerType type) {
		Objects.requireNonNull(handler, "You must register a non-null EventHandler");
		Objects.requireNonNull(type, "You must specify a ListenerType");
		eventHandlers.add(new RegisteredHandler(handler, type));
	}
	
	/**
	 * unregister your handler from receiving all events
	 * 
	 * @param handler	your event handler. Most used with <b>this</b>
	 */
	public void unregisterAllEvents(EventHandler handler) {
		if (handler != null) 
			unregisterEvents(handler, eventHandlers, eventHandlersToBeRemoved);
	}

	/**
	 * register your EventHandler to listen for message events, that are send 
	 * through the message bus.
	 * 
	 * @param handler	your event handler. Most used with <b>this</b>
	 * @param type		the listener type
	 */
	public void registerMessageEvents(EventHandler handler, ListenerType type) {
		Objects.requireNonNull(handler, "You must register a non-null EventHandler");
		Objects.requireNonNull(type, "You must specify a ListenerType");
		messageHandlers.add(new RegisteredHandler(handler, type));
	}
	
	/**
	 * unregister your handler from receiving message events
	 * 
	 * @param handler	your event handler. Most used with <b>this</b>
	 */
	public void unregisterMessageEvents(EventHandler handler) {
		if (handler != null) 
			unregisterEvents(handler, messageHandlers, messageHandlersToBeRemoved);
	}

	/**
	 * register your EventHandler to listen for request events, that are send 
	 * through the message bus.
	 * 
	 * @param handler	your event handler. Most used with <b>this</b>
	 * @param type		the listener type
	 */
	public void registerRequestEvents(EventHandler handler, ListenerType type) {
		Objects.requireNonNull(handler, "You must register a non-null EventHandler");
		Objects.requireNonNull(type, "You must specify a ListenerType");
		requestHandlers.add(new RegisteredHandler(handler, type));
	}
	
	/**
	 * unregister your handler from receiving request events
	 * 
	 * @param handler	your event handler. Most used with <b>this</b>
	 */
	public void unregisterRequestEvents(EventHandler handler) {
		if (handler != null) 
			unregisterEvents(handler, requestHandlers, requestHandlersToBeRemoved);
	}

	/**
	 * register your EventHandler to listen for data events, that are send 
	 * through the message bus.
	 * 
	 * @param handler	your event handler. Most used with <b>this</b>
	 * @param type		the listener type
	 */
	public void registerDataEvents(EventHandler handler, ListenerType type) {
		Objects.requireNonNull(handler, "You must register a non-null EventHandler");
		Objects.requireNonNull(type, "You must specify a ListenerType");
		dataHandlers.add(new RegisteredHandler(handler, type));
	}
	
	/**
	 * unregister your handler from receiving data events
	 * 
	 * @param handler	your event handler. Most used with <b>this</b>
	 */
	public void unregisterDataEvents(EventHandler handler) {
		if (handler != null) 
			unregisterEvents(handler, dataHandlers, dataHandlersToBeRemoved);
	}

	/**
	 * actual unregistering of a given handler.
	 * The list is checked, if the handler is in there and is stored in
	 * the to be removed list. The actual work is done in the runner thread
	 * @param handler
	 * @param list
	 * @param listToBeRemoved
	 */
	private static void unregisterEvents(EventHandler handler, 
			List<RegisteredHandler> list, List<RegisteredHandler> listToBeRemoved) {
		list.stream()
		.filter(rh -> rh.getHandler() == handler)
		.forEach(listToBeRemoved::add);
	}
	
	/**
	 * This inner class handles all events in its own thread. So the event
	 * handling is not running on the main thread.
	 * 
	 * Also the unregistering of handlers is done in this thread. If its done
	 * here, it is thread safe!
	 * 
	 * @author Dr. Jochen Raßler
	 *
	 */
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
					// TODO: why does poll return a null element?
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
						if (!Thread.interrupted())
							Thread.sleep(5);
					} catch (InterruptedException e) {
						// we are interrupted while waiting.
						// let's use this, to run this thread again
						// and sleep afterwards
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
		
		/**
		 * thread safe removal of unregistered handlers
		 */
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
		
		/**
		 * remove all handlers, that are garbage colltected without 
		 * unregistering or got null after registering.
		 */
		private void deleteNulls() {
			deleteNulls(eventHandlers);
			deleteNulls(messageHandlers);
			deleteNulls(requestHandlers);
			deleteNulls(dataHandlers);
		}
		
		/**
		 * remove null handlers
		 * @param list	the handler list to be cleared
		 */
		private void deleteNulls(List<RegisteredHandler> list) {
			while (list.contains(null)) 
				list.remove(list.indexOf(null));
		}
		
		/**
		 * call every EventHandler in this list and perform the handle method
		 * @param ev	the event to be processed
		 * @param list	the handler list to be processed
		 */
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

		/**
		 * send shutdown message to all handlers
		 */
		private void shutdown() {
			eventHandlers.stream().forEach(e -> e.getHandler().shutdown());
			messageHandlers.stream().forEach(e -> e.getHandler().shutdown());
			requestHandlers.stream().forEach(e -> e.getHandler().shutdown());
			dataHandlers.stream().forEach(e -> e.getHandler().shutdown());
		}
		
		/**
		 * stop the thread just before its next execution
		 */
		void stopRunner() {
			run = false;
		}
	}

} 
