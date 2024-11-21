package com.pablomonteserin.prueba.persistence.repositories;

import com.pablomonteserin.prueba.persistence.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);
}
