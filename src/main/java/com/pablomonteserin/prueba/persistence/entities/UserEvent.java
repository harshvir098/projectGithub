package com.pablomonteserin.prueba.persistence.entities;

import jakarta.persistence.*;

@Entity
public class UserEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // Invitee

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    private String content; // Content the user will bring to the event

    private String invitationStatus = "pending"; // default status is 'pending'

    @ManyToOne
    @JoinColumn(name = "inviter_id") // Link to inviter
    private User inviter; // User who sent the invitation

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
