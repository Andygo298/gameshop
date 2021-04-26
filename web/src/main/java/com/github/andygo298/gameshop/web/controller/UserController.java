package com.github.andygo298.gameshop.web.controller;

import com.github.andygo298.gameshop.model.entity.User;
import com.github.andygo298.gameshop.model.enums.Role;
import com.github.andygo298.gameshop.model.enums.Status;
import com.github.andygo298.gameshop.service.UserService;
import com.github.andygo298.gameshop.web.request.ForgotPasswordRequest;
import com.github.andygo298.gameshop.web.request.LoginRequest;
import com.github.andygo298.gameshop.web.request.RegistrationRequest;
import com.github.andygo298.gameshop.web.request.ResetPasswordRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
import java.util.function.Function;
import java.util.function.Supplier;

@RestController
@RequestMapping("/")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    private Supplier<ResponseStatusException> unauthorizedError = () -> {
        log.error("email or password is invalid");
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "email or password is invalid");
    };
    private Supplier<ResponseStatusException> activateCodeError = () -> {
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Activate code link is expired, or invalid");
    };
    private Supplier<ResponseStatusException> forgotPasswordCodeError = () -> {
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Code is expired, or invalid");
    };
    private Function<String, ResponseStatusException> userExists = (email) -> {
        log.error("User already exists - {}", email);
        throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists - " + email);
    };
    private Supplier<ResponseStatusException> notFoundUser = () -> {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No user available with this email.");
    };

    @GetMapping
    public String hello() {
        return "HELLO!!!";
    }

    @PostMapping("/auth/login")
    public ResponseEntity<User> login(@RequestBody LoginRequest loginRq) {
        User user = userService.login(loginRq.getEmail(), loginRq.getPassword())
                .orElseThrow(unauthorizedError);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                user, null, getAuthorities(user)
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
        log.info("User - {} was authenticated with role - {}.", user.getEmail(), user.getRole());
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PostMapping("/auth/registration")
    public ResponseEntity<User> registration(@RequestBody RegistrationRequest registrationRequest) {
        String registrationEmail = registrationRequest.getEmail();
        if (userService.findByEmail(registrationEmail).isPresent()) {
            log.info("User email - {} already exists.", registrationEmail);
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
        if (userService.saveActivateCode(userToSave)) {
            User user = userService.saveUser(userToSave);
            log.info("User - {} was created at : {}", user.getEmail(), LocalDateTime.now().toLocalDate());
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } else {
            log.error("User - {} was created at : {}.\nReason: {}", userToSave.getEmail(), LocalDateTime.now().toLocalDate(), "Save activate code error");
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
            log.info("user's ({}) status was changed to ACTIVE.", user.getEmail());
            return ResponseEntity.ok("User - " + user.getEmail() + " is activated.");
        }
        log.info("Activate code - {} is expired or invalid", activateCode);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/auth/forgot_password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        if (userService.saveForgotPasswordCode(forgotPasswordRequest.getEmail())) {
            log.info("Email with forgot password's code was sent to - {}", forgotPasswordRequest.getEmail());
            return ResponseEntity.ok("Email was sent to " + forgotPasswordRequest.getEmail());
        } else {
            log.error("Save forgot-code error.");
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
            log.info("User's ({}) password was changed.", userEmail);
            return ResponseEntity.ok(userService.saveUser(userToSave));
        } else {
            log.error("Reset password code - {} error.\nReason: Reset code is expired or invalid.", resetPasswordRequest.getUserCode());
            throw forgotPasswordCodeError.get();
        }
    }

    @GetMapping("/auth/check_code")
    public ResponseEntity<String> checkCode(@RequestParam String userCode) {
        String emailByCode = userService.getByForgotPasswordCode(userCode);
        if (Objects.nonNull(emailByCode)) {
            log.info("Forgot password code - {} is OK!", userCode);
            return ResponseEntity.ok(userCode);
        } else {
            log.error("Forgot password code - {} error.\nReason: Reset code is expired or invalid.", userCode);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/auth/logout")
    public ResponseEntity<String> doGet(HttpServletRequest rq) {
        SecurityContextHolder.clearContext();
        try {
            String email = ((User) SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal()).getEmail();
            rq.logout();
            log.info("User - {} was logout.", email);
        } catch (ServletException e) {
            log.error("---User wasn't logout. ERROR servlet exception.");
            new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.RESET_CONTENT);
    }

    private List<GrantedAuthority> getAuthorities(User user) {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }
}
