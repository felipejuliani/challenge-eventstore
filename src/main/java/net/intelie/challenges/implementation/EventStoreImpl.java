package net.intelie.challenges.implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import net.intelie.challenges.Event;
import net.intelie.challenges.EventIterator;
import net.intelie.challenges.EventStore;
import net.intelie.challenges.db.EventRepository;

public class EventStoreImpl implements EventStore {

	/**
     * Insert an event into HashSet memory repository to ensure one thread at time
     * writing data in repository. 
     */
	@Override
	public void insert(Event event) {
		if (EventRepository.events.add(event)) {
			System.out.println("Successful event [" + event.type() + " - " + event.timestamp() + "] insertion.");	
		}
	}

	/**
     * Remove all events presents in memory repository and in iterator list by its type.
     * This method has been thread-safed to ensure that one thread at time removes
     * the listed events.
     */
	@Override
	public synchronized void removeAll(String type) {
		long count = 0L;
    	for (EventIterator it = this.query(type); it.moveNext(); ) {
    		EventRepository.events.remove(it.current());
    		it.remove();
            count++;
        }
    	System.out.println("All " + count + " " + type +" events have been successful removed!");
	}

	/**
     * List events by type, start time and end time.
     * The Java 8 Stream API is being used to get the list from memory repository and
     * assign it in EventIterator object.
     */
	@Override
	public EventIterator query(String type, long startTime, long endTime) {
		System.out.println("Executing a query...");
		List<Event> events = new ArrayList<Event>();
		
		EventIteratorImpl eventIteratorImpl = new EventIteratorImpl();
		
		EventRepository.events.stream().forEach(
            event -> {
            	Event e = selectEvent(event, type, startTime, endTime);
                if (e != null) {
                	events.add(e);
                }
            }
        );
		
		eventIteratorImpl.it = events.iterator();
		
		return eventIteratorImpl;
	}
	
	/**
     * Overload query method that list events just by type.
     */
	public EventIterator query(String type) {
		System.out.println("Executing a query...");
		List<Event> events = new ArrayList<Event>();
		
		EventIteratorImpl eventIteratorImpl = new EventIteratorImpl();
			
		EventRepository.events.stream().forEach(
            event -> {
            	Event e = selectEvent(event, type);
                if (e != null) {
                	events.add(e);
                }
            }
        );
		
		eventIteratorImpl.it = events.iterator();
		
		return eventIteratorImpl;
	}
	
	/**
     * List all events in memory repository using Java 8 Stram API.
     */
	public List<Event> listAllEvents() {
		return EventRepository.events.stream().map(event -> event).collect(Collectors.toList());
	}
	
	/**
     * Count all events in memory repository using Java 8 Stram API.
     */
	public long countEvents() {
		return EventRepository.events.stream().count();
	}
	
	
	/**
     * Auxiliary method to select an event by a condition
     */
	private Event selectEvent(Event event, String type, long startTime, long endTime) {
		if (event.type().equals(type) && event.timestamp() >= startTime && event.timestamp() <= endTime ) {
			return event;
		}
		return null;
	}
	
	/**
     * Overload auxiliary method to select an event by a condition
     */
	private Event selectEvent(Event event, String type) {
		if (event.type().equals(type)) {
			return event;
		}
		return null;
	}
}
