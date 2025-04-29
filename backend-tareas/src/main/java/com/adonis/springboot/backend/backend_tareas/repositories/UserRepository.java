package com.adonis.springboot.backend.backend_tareas.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adonis.springboot.backend.backend_tareas.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);
}

