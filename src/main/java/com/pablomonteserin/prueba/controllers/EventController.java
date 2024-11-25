package com.pablomonteserin.prueba.controllers;
import com.pablomonteserin.prueba.dto.EventDetailsResponse;

import com.pablomonteserin.prueba.persistence.entities.Event;
import com.pablomonteserin.prueba.persistence.entities.User;
import com.pablomonteserin.prueba.persistence.entities.UserEvent;
import com.pablomonteserin.prueba.persistence.repositories.EventRepository;
import com.pablomonteserin.prueba.persistence.repositories.UserEventRepository;
import com.pablomonteserin.prueba.persistence.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserEventRepository userEventRepository;

    /**
     * Helper method to fetch UserEvent by user and event.
     * @param userId The ID of the user.
     * @param eventId The ID of the event.
     * @return An Optional containing the UserEvent if found.
     */
    private Optional<UserEvent> getUserEvent(Integer userId, Integer eventId) {
        Optional<User> user = userRepository.findById(userId);
        Optional<Event> event = eventRepository.findById(eventId);

        if (user.isPresent() && event.isPresent()) {
            return userEventRepository.findByUserAndEvent(user.get(), event.get());
        }
        return Optional.empty();
    }

    @GetMapping("/{eventId}/details")
    public ResponseEntity<?> getEventDetails(@PathVariable int eventId) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);

        if (eventOptional.isPresent()) {
            Event event = eventOptional.get();
            List<UserEvent> userEvents = userEventRepository.findByEventId(eventId);

            // Return both event details and user events
            return ResponseEntity.ok(new EventDetailsResponse(event, userEvents));
        }
        return ResponseEntity.status(404).body("Event not found");
    }



    /**
     * Create a new event.
     * @param event The event to be created.
     * @return The created event.
     */
    @PostMapping("/create")
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (loggedInUser != null) {
            event.setCreator(loggedInUser); // Set the logged-in user as the creator
            eventRepository.save(event);
            return ResponseEntity.ok(event);
        }
        return ResponseEntity.status(401).body(null);
    }

    /**
     * Invite a participant to an event.
     * @param eventId The ID of the event.
     * @param userId The ID of the user to invite.
     * @param content Optional content to include with the invitation.
     * @return A message indicating the success or failure of the invitation.
     */
    @PostMapping("/{eventId}/invite/{userId}")
    public ResponseEntity<String> inviteParticipant(
            @PathVariable Integer eventId,
            @PathVariable Integer userId,
            @RequestParam(required = false) String content) {
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Optional<Event> eventOptional = eventRepository.findById(eventId);
        Optional<User> userOptional = userRepository.findById(userId);

        if (eventOptional.isPresent() && userOptional.isPresent() && loggedInUser != null) {
            Event event = eventOptional.get();
            User user = userOptional.get();

            UserEvent userEvent = new UserEvent();
            userEvent.setUser(user);
            userEvent.setEvent(event);
            userEvent.setInviter(loggedInUser);
            if (content != null) userEvent.setContent(content); // Set optional content
            userEventRepository.save(userEvent);

            return ResponseEntity.ok("Participant invited successfully by " + loggedInUser.getUsername());
        }

        return ResponseEntity.status(404).body("Event or User not found");
    }

    /**
     * Accept an invitation to an event.
     * @param eventId The ID of the event.
     * @param userId The ID of the user accepting the invitation.
     * @return A message indicating the success or failure of the operation.
     */
    @PostMapping("/{eventId}/accept/{userId}")
    public ResponseEntity<String> acceptInvitation(@PathVariable Integer eventId, @PathVariable Integer userId) {
        Optional<UserEvent> userEventOptional = getUserEvent(userId, eventId);

        if (userEventOptional.isPresent()) {
            UserEvent userEvent = userEventOptional.get();
            userEvent.setInvitationStatus("accepted");
            userEventRepository.save(userEvent);
            return ResponseEntity.ok("Invitation accepted");
        }
        return ResponseEntity.status(404).body("UserEvent not found");
    }

    /**
     * Decline an invitation to an event.
     * @param eventId The ID of the event.
     * @param userId The ID of the user declining the invitation.
     * @return A message indicating the success or failure of the operation.
     */
    @PostMapping("/{eventId}/decline/{userId}")
    public ResponseEntity<String> declineInvitation(@PathVariable Integer eventId, @PathVariable Integer userId) {
        Optional<UserEvent> userEventOptional = getUserEvent(userId, eventId);

        if (userEventOptional.isPresent()) {
            UserEvent userEvent = userEventOptional.get();
            userEvent.setInvitationStatus("declined");
            userEventRepository.save(userEvent);
            return ResponseEntity.ok("Invitation declined");
        }
        return ResponseEntity.status(404).body("UserEvent not found");
    }

    /**
     * Get all pending invitations for a user.
     * @param userId The ID of the user.
     * @return A list of pending invitations.
     */
    @GetMapping("/{userId}/invitations")
    public ResponseEntity<List<UserEvent>> getUserInvitations(@PathVariable Integer userId) {
        List<UserEvent> userEvents = userEventRepository.findByUserIdAndInvitationStatus(userId, "pending");
        return ResponseEntity.ok(userEvents);
    }

    /**
     * Get all events in the system.
     * @return A list of all events.
     */
    @GetMapping("/all")
    public ResponseEntity<Iterable<Event>> getAllEvents() {
        return ResponseEntity.ok(eventRepository.findAll());
    }
    
    @PutMapping("/{eventId}/update/{userId}")
    public ResponseEntity<String> updateUserEventContent(@PathVariable Integer eventId, 
                                                         @PathVariable Integer userId, 
                                                         @RequestBody String content) {
        List<UserEvent> userEventList = userEventRepository.findByUserIdAndInvitationStatus(userId, "accepted");

        if (!userEventList.isEmpty()) {
            // Assuming you want to update the first matching UserEvent
            UserEvent userEvent = userEventList.get(0);  
            userEvent.setContent(content);  // Update the content
            userEventRepository.save(userEvent);  // Save the updated UserEvent
            return ResponseEntity.ok("Content updated successfully");
        }

        return ResponseEntity.status(404).body("UserEvent not found or not accepted");
    }

    @GetMapping("/{eventId}/accepted-users")
    public ResponseEntity<List<User>> getAcceptedUsers(@PathVariable Integer eventId) {
        // Fetch the users who have accepted the invitation for the event
        List<UserEvent> userEvents = userEventRepository.findByEventIdAndInvitationStatus(eventId, "accepted");

        // Extract the users from the UserEvent objects
        List<User> acceptedUsers = userEvents.stream()
                                             .map(UserEvent::getUser)
                                             .collect(Collectors.toList());

        // If no users have accepted the invitation
        if (acceptedUsers.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // Return the list of accepted users
        return ResponseEntity.ok(acceptedUsers);
    }



}
