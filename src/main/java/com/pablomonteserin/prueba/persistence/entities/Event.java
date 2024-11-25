package com.pablomonteserin.prueba.persistence.entities;

import java.time.LocalDate;
import jakarta.persistence.*;

@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private LocalDate date;
    private String location;

    @ManyToOne
    @JoinColumn(name = "creator_id") // Foreign key linking to the User table
    private User creator; // User who created the event

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

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    // Optionally, you can still expose the creatorId via a getter if needed
    public int getCreatorId() {
        return creator != null ? creator.getId() : 0; // Safely return creatorId if creator is not null
    }
}
