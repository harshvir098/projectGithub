package com.pablomonteserin.prueba.service;

import com.pablomonteserin.prueba.persistence.entities.Event;
import com.pablomonteserin.prueba.persistence.entities.User;
import com.pablomonteserin.prueba.persistence.entities.UserEvent;
import com.pablomonteserin.prueba.persistence.repositories.EventRepository;
import com.pablomonteserin.prueba.persistence.repositories.UserEventRepository;
import com.pablomonteserin.prueba.persistence.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class EventService {

	private final EventRepository eventRepository;
	private final UserRepository userRepository;
	private final UserEventRepository userEventRepository;

	@Autowired
	public EventService(EventRepository eventRepository, UserRepository userRepository,
			UserEventRepository userEventRepository) {
		this.eventRepository = eventRepository;
		this.userRepository = userRepository;
		this.userEventRepository = userEventRepository;
	}

	public Event createEvent(String name, String date) {
		Event event = new Event();
		event.setName(name);
		event.setDate(LocalDate.parse(date)); // Convert string to LocalDate
		return eventRepository.save(event);
	}

	public UserEvent addParticipantToEvent(int eventId, int userId) {
		Optional<Event> event = eventRepository.findById(eventId);
		Optional<User> user = userRepository.findById(userId);

		if (event.isPresent() && user.isPresent()) {
			UserEvent userEvent = new UserEvent();
			userEvent.setEvent(event.get());
			userEvent.setUser(user.get());
			return userEventRepository.save(userEvent);
		}

		return null;
	}

	public UserEvent addContentToEvent(int eventId, int userId, String content) {
		Optional<UserEvent> userEventOpt = userEventRepository.findByUserAndEvent(userRepository.findById(userId).get(),
				eventRepository.findById(eventId).get());

		if (userEventOpt.isPresent()) {
			UserEvent userEvent = userEventOpt.get();
			userEvent.setContent(content);
			return userEventRepository.save(userEvent);
		}

		return null;
	}
}
