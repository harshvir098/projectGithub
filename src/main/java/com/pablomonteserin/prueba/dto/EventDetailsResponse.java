package com.pablomonteserin.prueba.dto;

import com.pablomonteserin.prueba.persistence.entities.Event;
import com.pablomonteserin.prueba.persistence.entities.UserEvent;

import java.util.List;

public class EventDetailsResponse {
    private Event event;
    private List<UserEvent> userEvents;

    public EventDetailsResponse(Event event, List<UserEvent> userEvents) {
        this.event = event;
        this.userEvents = userEvents;
    }

    // Getters and setters
    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public List<UserEvent> getUserEvents() {
        return userEvents;
    }

    public void setUserEvents(List<UserEvent> userEvents) {
        this.userEvents = userEvents;
    }
}
