package com.pablomonteserin.prueba.persistence.entities;

import jakarta.persistence.*;

@Entity
public class UserEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // Invitee (the user being invited to the event)

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event; // The event the user is invited to

    private String content; // Additional content the user may bring to the event

    private String invitationStatus = "pending"; // Default status is "pending" until accepted or declined

    @ManyToOne
    @JoinColumn(name = "inviter_id") // Link to inviter (user who sends the invitation)
    private User inviter;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getInvitationStatus() {
        return invitationStatus;
    }

    public void setInvitationStatus(String invitationStatus) {
        this.invitationStatus = invitationStatus;
    }

    public User getInviter() {
        return inviter;
    }

    public void setInviter(User inviter) {
        this.inviter = inviter;
    }
}
