package com.adonis.springboot.backend.backend_tareas.controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adonis.springboot.backend.backend_tareas.dto.AuthRequest;
import com.adonis.springboot.backend.backend_tareas.dto.AuthResponse;
import com.adonis.springboot.backend.backend_tareas.model.User;
import com.adonis.springboot.backend.backend_tareas.repositories.UserRepository;
import com.adonis.springboot.backend.backend_tareas.security.JWTUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JWTUtil  jwtUtil;

  @PostMapping("/register")
  public ResponseEntity<AuthResponse> register(@RequestBody AuthRequest request) {
    if (userRepository.findByEmail(request.getEmail()).isPresent()) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
    }

    User user = new User();
    user.setEmail(request.getEmail());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    userRepository.save(user);

    String token = jwtUtil.generateToken(user);
    return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(token, user.getEmail()));
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
    User user = userRepository.findByEmail(request.getEmail())
      .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    String token = jwtUtil.generateToken(user);
    return ResponseEntity.ok(new AuthResponse(token, user.getEmail()));
  }

  @GetMapping("/me")
  public ResponseEntity<?> me(@AuthenticationPrincipal User user) {
    return ResponseEntity.ok(new AuthResponse(null, user.getEmail()));
  }
}
