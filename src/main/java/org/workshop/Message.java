package org.workshop;

public class Message {
    private final Event event;
    private final String payload;
    private final SubEvent subEvent;

    public Event getEvent() {
        return event;
    }

    public String getPayload() {
        return payload;
    }

    public SubEvent getSubEvent() {
        return subEvent;
    }

    public Message(Event event, String payload, SubEvent subEvent) {
        this.event = event;
        this.payload = payload;
        this.subEvent = subEvent;
    }
}
