package com.github.andygo298.gameshop.web.controller;

import com.github.andygo298.gameshop.model.entity.User;
import com.github.andygo298.gameshop.model.enums.Role;
import com.github.andygo298.gameshop.model.enums.Status;
import com.github.andygo298.gameshop.service.UserService;
import com.github.andygo298.gameshop.web.request.ForgotPasswordRequest;
import com.github.andygo298.gameshop.web.request.LoginRequest;
import com.github.andygo298.gameshop.web.request.RegistrationRequest;
import com.github.andygo298.gameshop.web.request.ResetPasswordRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
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
    Supplier<ResponseStatusException> forgotPasswordCodeError = () -> {
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Code is expired, or invalid");
    };

    Function<String, ResponseStatusException> userExists = (email) -> {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists - " + email);
    };

    Supplier<ResponseStatusException> notFoundUser = () -> {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No user available with this email.");
    };

    @GetMapping
    public String hello() {
        return "HELLO!!!";
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody LoginRequest loginRq) {
        User user = userService.login(loginRq.getEmail(), loginRq.getPassword())
                .orElseThrow(unauthorizedError);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                user.getEmail(), passwordEncoder.encode(user.getPassword()), getAuthorities(user)
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PostMapping("/registration")
    public ResponseEntity<User> registration(@RequestBody RegistrationRequest registrationRequest) {
        String registrationEmail = registrationRequest.getEmail();
        if (userService.findByEmail(registrationEmail).isPresent()) {
            throw userExists.apply(registrationEmail);
        }
        User userToSave = User.builder()
                .firstName(registrationRequest.getFirstName())
                .lastName(registrationRequest.getLastName())
                .email(registrationRequest.getEmail())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .role(Role.valueOf(registrationRequest.getRole().toString()))
                .status(Status.BANNED)
                .createdAt(LocalDateTime.now().toLocalDate())
                .build();
        if(userService.saveActivateCode(userToSave)){
            User user = userService.saveUser(userToSave);
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        }else {
         throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Save activate code error");
        }
    }

    @GetMapping("/auth/confirm")
    public ResponseEntity<String> registration(@RequestParam String activateCode) {
        if (Objects.nonNull(activateCode)) {
            User user = userService.activateUserByCode(activateCode)
                    .orElseThrow(activateCodeError);
            user.setStatus(Status.ACTIVE);
            userService.saveUser(user);
            return ResponseEntity.ok("User - " + user.getEmail() + " is activated.");
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/auth/forgot_password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        if (userService.saveForgotPasswordCode(forgotPasswordRequest.getEmail())){
            return ResponseEntity.ok("Email was sent to " + forgotPasswordRequest.getEmail());
        }else {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Save forgot-code error");
        }
    }

    @PostMapping("/auth/reset")
    public ResponseEntity<User> resetUserPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        String userEmail = userService.getByForgotPasswordCode(resetPasswordRequest.getUserCode());
        if (Objects.nonNull(userEmail)) {
            User userToSave = userService.findByEmail(userEmail)
                    .orElseThrow(notFoundUser);
            userToSave.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
            return ResponseEntity.ok(userService.saveUser(userToSave));
        } else {
            throw forgotPasswordCodeError.get();
        }
    }

    @GetMapping("/auth/check_code")
    public ResponseEntity<String> checkCode(@RequestParam String userCode) {
        String emailByCode = userService.getByForgotPasswordCode(userCode);
        if (Objects.nonNull(emailByCode)) {
            return ResponseEntity.ok(userCode);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/auth/logout")
    public ResponseEntity<String> doGet(HttpServletRequest rq) {
        SecurityContextHolder.clearContext();
        try {
            rq.logout();
        } catch (ServletException e) {
            new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.RESET_CONTENT);
    }

    private List<GrantedAuthority> getAuthorities(User user) {
        return Collections.singletonList((GrantedAuthority) () -> "ROLE_" + user.getRole().name());
    }
}
