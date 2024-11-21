package com.pablomonteserin.prueba.persistence.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;  // Use 'int' for the event id

    private String name;
    private LocalDate date;

    @OneToMany(mappedBy = "event")
    private List<UserEvent> participants;  // List of participants associated with the event

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate localDate) {
        this.date = localDate;
    }

    public List<UserEvent> getParticipants() {
        return participants;
    }

    public void setParticipants(List<UserEvent> participants) {
        this.participants = participants;
    }
}
