package com.pablomonteserin.prueba.persistence.repositories;

import com.pablomonteserin.prueba.persistence.entities.UserEvent;
import com.pablomonteserin.prueba.persistence.entities.User;
import com.pablomonteserin.prueba.persistence.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserEventRepository extends JpaRepository<UserEvent, Integer> {

    Optional<UserEvent> findByUserAndEvent(User user, Event event);

    List<UserEvent> findByEventId(int eventId);

    List<UserEvent> findByUserIdAndInvitationStatus(int userId, String invitationStatus);
    List<UserEvent> findByEventIdAndInvitationStatus(int eventId, String invitationStatus);

}
