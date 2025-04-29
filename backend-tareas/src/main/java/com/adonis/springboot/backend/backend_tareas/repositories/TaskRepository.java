package com.adonis.springboot.backend.backend_tareas.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adonis.springboot.backend.backend_tareas.model.Task;
import com.adonis.springboot.backend.backend_tareas.model.User;

public interface TaskRepository extends JpaRepository<Task, Long> {
  List<Task> findByUser(User user);
}
