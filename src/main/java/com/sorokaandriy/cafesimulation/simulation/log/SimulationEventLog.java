package com.sorokaandriy.cafesimulation.simulation.log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimulationEventLog {
    public static class Event {
        public final long tick;
        public final String message;

        public Event(long tick, String message) {
            this.tick = tick;
            this.message = message;
        }

        @Override
        public String toString() {
            return "[" + tick + "] " + message;
        }
    }

    private final List<Event> events = new ArrayList<>();

    public void add(long tick, String message) {
        events.add(new Event(tick, message));
    }

    public List<Event> getEvents() {
        return Collections.unmodifiableList(events);
    }


    public List<Event> getEventsSince(int fromIndex) {
        if (fromIndex >= events.size()) return Collections.emptyList();
        return Collections.unmodifiableList(events.subList(fromIndex, events.size()));
    }

    public int size() {
        return events.size();
    }
}
