package com.adonis.springboot.backend.backend_tareas.controllers;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.adonis.springboot.backend.backend_tareas.model.Task;
import com.adonis.springboot.backend.backend_tareas.model.User;
import com.adonis.springboot.backend.backend_tareas.repositories.TaskRepository;
import com.adonis.springboot.backend.backend_tareas.repositories.UserRepository;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    private User getAuthenticatedUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));
    }

    @GetMapping
    public List<Task> getAll(@AuthenticationPrincipal UserDetails userDetails) {
        User user = getAuthenticatedUser(userDetails);
        return taskRepository.findByUser(user);
    }

    @PostMapping
    public Task create(@RequestBody Task task, @AuthenticationPrincipal UserDetails userDetails) {
        User user = getAuthenticatedUser(userDetails);
        task.setUser(user);
        return taskRepository.save(task);
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<?> toggle(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        User user = getAuthenticatedUser(userDetails);
        Task task = taskRepository.findById(id)
            .filter(t -> t.getUser().getId().equals(user.getId()))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        task.setCompleted(!task.isCompleted());
        return ResponseEntity.ok(taskRepository.save(task));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        User user = getAuthenticatedUser(userDetails);
        Task task = taskRepository.findById(id)
            .filter(t -> t.getUser().getId().equals(user.getId()))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        taskRepository.delete(task);
        return ResponseEntity.ok().build();
    }
}