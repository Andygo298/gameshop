package com.github.andygo298.gameshop.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.andygo298.gameshop.model.entity.User;
import com.github.andygo298.gameshop.model.enums.Role;
import com.github.andygo298.gameshop.model.enums.Status;
import com.github.andygo298.gameshop.service.UserService;
import com.github.andygo298.gameshop.web.request.LoginRequest;
import com.github.andygo298.gameshop.web.request.RegistrationRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

@RestController
@CrossOrigin(origins = "http://localhost:80/gameshop")
public class UserController {

    private UserService userService;
    private PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    Supplier<ResponseStatusException> unauthorizedError = () -> {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "email or password is invalid");
    };
    Supplier<ResponseStatusException> activateCodeError = () -> {
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Activate code link is expired, or invalid");
    };

    @GetMapping
    public String hello() {
        return "HELLO!!!";
    }

    @GetMapping("/login")
    public ResponseEntity<HttpHeaders> login() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        HttpHeaders headers = new HttpHeaders();
        if (authentication == null || "anonymousUser".equals(authentication.getPrincipal())) {
            headers.setLocation(URI.create("/home"));
        } else {
            headers.setLocation(URI.create("/login"));
        }
        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody LoginRequest loginRq) {
        Optional<User> optionalUserByLogin = userService.login(loginRq.getEmail(), loginRq.getPassword());
        if (optionalUserByLogin.isPresent()) {
            User user = optionalUserByLogin.get();
            Authentication auth = new UsernamePasswordAuthenticationToken(user, null, getAuthorities(user));
            SecurityContextHolder.getContext().setAuthentication(auth);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else throw unauthorizedError.get();
    }

    @PostMapping("/registration")
    public ResponseEntity<User> registration(@RequestBody RegistrationRequest registrationRequest) {
        User userToSave = User.builder()
                .firstName(registrationRequest.getFirstName())
                .lastName(registrationRequest.getLastName())
                .email(registrationRequest.getEmail())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .role(Role.valueOf(registrationRequest.getRole().toString()))
                .status(Status.BANNED)
                .createdAt(LocalDateTime.now().toLocalDate())
                .build();
        userService.saveActivateCode(userToSave);
        User user = userService.saveUser(userToSave);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @GetMapping("/registration")
    public ResponseEntity<User> registration(@RequestParam String activateCode) {
        String userEmail = userService.getActivateCode(activateCode);
        if (Objects.nonNull(userEmail)) {
            Optional<User> userByEmail = userService.findByEmail(userEmail);
            if (userByEmail.isPresent()) {
                User user = userByEmail.get();
                user.setStatus(Status.ACTIVE);
                userService.saveUser(user);
                return new ResponseEntity<>(HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            throw activateCodeError.get();
        }
    }

    private List<GrantedAuthority> getAuthorities(User user) {
        return Collections.singletonList((GrantedAuthority) () -> "ROLE_" + user.getRole().name());
    }

}
