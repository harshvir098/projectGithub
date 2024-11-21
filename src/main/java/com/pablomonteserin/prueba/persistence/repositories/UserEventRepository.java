package com.pablomonteserin.prueba.persistence.repositories;

import com.pablomonteserin.prueba.persistence.entities.UserEvent;
import com.pablomonteserin.prueba.persistence.entities.User;
import com.pablomonteserin.prueba.persistence.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserEventRepository extends JpaRepository<UserEvent, Integer> {

    Optional<UserEvent> findByUserAndEvent(User user, Event event);

    List<UserEvent> findByUserIdAndInvitationStatus(Integer userId, String invitationStatus);
}
