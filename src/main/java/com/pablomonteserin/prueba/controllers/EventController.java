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

import java.util.List;
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
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (loggedInUser != null) {
            event.setCreator(loggedInUser); // Set the logged-in user as the creator of the event
            eventRepository.save(event);
            return ResponseEntity.ok(event);
        }
        return ResponseEntity.status(401).body(null);
    }

 // Invite Participant and Return Inviter's Name
    @PostMapping("/{eventId}/invite/{userId}")
    public ResponseEntity<String> inviteParticipant(@PathVariable Integer eventId, @PathVariable Integer userId) {
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Optional<Event> eventOptional = eventRepository.findById(eventId);
        Optional<User> userOptional = userRepository.findById(userId);

        if (eventOptional.isPresent() && userOptional.isPresent() && loggedInUser != null) {
            Event event = eventOptional.get();
            User user = userOptional.get();

            // Create a new UserEvent to represent the invitation
            UserEvent userEvent = new UserEvent();
            userEvent.setUser(user); // Set the invitee
            userEvent.setEvent(event); // Set the event
            userEvent.setInviter(loggedInUser); // Set the logged-in user as the inviter

            // Save the invitation to the UserEvent table
            userEventRepository.save(userEvent);

            // Return a response with the inviter's name
            return ResponseEntity.ok("Participant invited successfully by " + loggedInUser.getUsername());
        }

        return ResponseEntity.status(404).body("Event or User not found");
    }


    // Accept invitation
    @PostMapping("/{eventId}/accept/{userId}")
    public ResponseEntity<String> acceptInvitation(@PathVariable Integer eventId, @PathVariable Integer userId) {
        Optional<UserEvent> userEventOptional = userEventRepository.findByUserAndEvent(userRepository.findById(userId).get(), eventRepository.findById(eventId).get());

        if (userEventOptional.isPresent()) {
            UserEvent userEvent = userEventOptional.get();
            userEvent.setInvitationStatus("accepted");
            userEventRepository.save(userEvent);

            return ResponseEntity.ok("Invitation accepted");
        }
        return ResponseEntity.status(404).body("UserEvent not found");
    }

    // Decline invitation
    @PostMapping("/{eventId}/decline/{userId}")
    public ResponseEntity<String> declineInvitation(@PathVariable Integer eventId, @PathVariable Integer userId) {
        Optional<UserEvent> userEventOptional = userEventRepository.findByUserAndEvent(userRepository.findById(userId).get(), eventRepository.findById(eventId).get());

        if (userEventOptional.isPresent()) {
            UserEvent userEvent = userEventOptional.get();
            userEvent.setInvitationStatus("declined");
            userEventRepository.save(userEvent);

            return ResponseEntity.ok("Invitation declined");
        }
        return ResponseEntity.status(404).body("UserEvent not found");
    }

    // Get all events
    @GetMapping("/all")
    public ResponseEntity<Iterable<Event>> getAllEvents() {
        Iterable<Event> events = eventRepository.findAll();
        return ResponseEntity.ok(events);
    }

    // Get user invitations
    @GetMapping("/{userId}/invitations")
    public ResponseEntity<List<UserEvent>> getUserInvitations(@PathVariable Integer userId) {
        List<UserEvent> userEvents = userEventRepository.findByUserIdAndInvitationStatus(userId, "pending");
        return ResponseEntity.ok(userEvents);
    }
}
