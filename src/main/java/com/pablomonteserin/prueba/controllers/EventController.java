package com.pablomonteserin.prueba.controllers;

import com.pablomonteserin.prueba.persistence.entities.Event;
import com.pablomonteserin.prueba.persistence.entities.User;
import com.pablomonteserin.prueba.persistence.entities.UserEvent;
import com.pablomonteserin.prueba.persistence.repositories.EventRepository;
import com.pablomonteserin.prueba.persistence.repositories.UserEventRepository;
import com.pablomonteserin.prueba.persistence.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserEventRepository userEventRepository;

    // Create a new event
    @PostMapping("/create")
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        // Get the currently authenticated user
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (loggedInUser != null) {
            // Save the event (we can associate the logged-in user as the creator if needed)
            eventRepository.save(event);
            return ResponseEntity.ok(event);
        }
        return ResponseEntity.status(401).body(null);  // Unauthorized if no user is logged in
    }

    // Invite participant to an event
    @PostMapping("/{eventId}/invite/{userId}")
    public ResponseEntity<String> inviteParticipant(@PathVariable Integer eventId, @PathVariable Integer userId) {  // Use Integer instead of Long
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        Optional<User> userOptional = userRepository.findById(userId);

        if (eventOptional.isPresent() && userOptional.isPresent()) {
            Event event = eventOptional.get();
            User user = userOptional.get();

            UserEvent userEvent = new UserEvent();
            userEvent.setUser(user);
            userEvent.setEvent(event);
            userEventRepository.save(userEvent);

            return ResponseEntity.ok("Participant invited successfully");
        }
        return ResponseEntity.status(404).body("Event or User not found");
    }

    // Add content (what the user will bring) to an event
    @PostMapping("/{eventId}/addContent/{userId}")
    public ResponseEntity<String> addContentToEvent(@PathVariable Integer eventId, @PathVariable Integer userId, @RequestBody String content) {  // Use Integer instead of Long
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        Optional<User> userOptional = userRepository.findById(userId);

        if (eventOptional.isPresent() && userOptional.isPresent()) {
            Event event = eventOptional.get();
            User user = userOptional.get();

            Optional<UserEvent> userEventOptional = userEventRepository.findByUserAndEvent(user, event);
            if (userEventOptional.isPresent()) {
                UserEvent userEvent = userEventOptional.get();
                userEvent.setContent(content);  // Set the content the user is bringing
                userEventRepository.save(userEvent);
                return ResponseEntity.ok("Content added to event");
            }
            return ResponseEntity.status(404).body("User is not invited to this event");
        }
        return ResponseEntity.status(404).body("Event or User not found");
    }
    
    @GetMapping("/all")
    public ResponseEntity<Iterable<Event>> getAllEvents() {
        Iterable<Event> events = eventRepository.findAll();  // Fetch all events from the database
        return ResponseEntity.ok(events); 
    }
}
