package com.github.andygo298.gameshop.web.controller;

import com.github.andygo298.gameshop.model.entity.User;
import com.github.andygo298.gameshop.model.enums.Role;
import com.github.andygo298.gameshop.model.enums.Status;
import com.github.andygo298.gameshop.service.UserService;
import com.github.andygo298.gameshop.web.config.SwaggerConfig;
import com.github.andygo298.gameshop.web.controller.util.ExceptionMessagesUtil;
import com.github.andygo298.gameshop.web.request.ForgotPasswordRequest;
import com.github.andygo298.gameshop.web.request.LoginRequest;
import com.github.andygo298.gameshop.web.request.RegistrationRequest;
import com.github.andygo298.gameshop.web.request.ResetPasswordRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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

@RestController
@RequestMapping("/")
@Api(tags = { SwaggerConfig.TAG_1 })
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String hello() {
        return "HELLO!!!";
    }

    @ApiOperation("Login")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "User was successfully authenticated."),
                    @ApiResponse(code = 401, message = "You are not authorized or your account isn't activated.")
            }
    )
    @PostMapping("/auth/login")
    public ResponseEntity<User> login(@RequestBody LoginRequest loginRq) {
        User user = userService.login(loginRq.getEmail(), loginRq.getPassword())
                .orElseThrow(ExceptionMessagesUtil.unauthorizedError);
        if (user.getStatus().equals(Status.BANNED)){
            throw ExceptionMessagesUtil.userIsNotActivated.apply(loginRq.getEmail());
        }
        Authentication auth = new UsernamePasswordAuthenticationToken(
                user, null, getAuthorities(user)
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
        log.info("User - {} was authenticated with role - {}.", user.getEmail(), user.getRole());
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @ApiOperation("Create a new User.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 201, message = "User was successfully created and Email with activate code sent."),
                    @ApiResponse(code = 400, message = "User already exists."),
                    @ApiResponse(code = 500, message = "Save activate code error")
            }
    )
    @PostMapping("/auth/registration")
    public ResponseEntity<User> registration(@RequestBody RegistrationRequest registrationRequest) {
        String registrationEmail = registrationRequest.getEmail();
        if (userService.findByEmail(registrationEmail).isPresent()) {
            log.info("User email - {} already exists.", registrationEmail);
            throw ExceptionMessagesUtil.userExists.apply(registrationEmail);
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
            log.error("User - {} was created at : {}.\nReason: {}", userToSave.getEmail(),
                    LocalDateTime.now().toLocalDate(), "Save activate code error");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Save activate code error");
        }
    }

    @ApiOperation("Activates user using the code passed as part of the request.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "User was successfully activated."),
                    @ApiResponse(code = 204, message = "Activate code not found."),
                    @ApiResponse(code = 500, message = "Activate code link is invalid.")
            }
    )
    @GetMapping("/auth/confirm")
    public ResponseEntity<String> registration(@RequestParam String activateCode) {
        if (Objects.nonNull(activateCode)) {
            User user = userService.activateUserByCode(activateCode)
                    .orElseThrow(ExceptionMessagesUtil.activateCodeError);
            user.setStatus(Status.ACTIVE);
            userService.saveUser(user);
            log.info("user's ({}) status was changed to ACTIVE.", user.getEmail());
            return ResponseEntity.ok("User - " + user.getEmail() + " is activated.");
        } else {
            log.info("Activate code - {} is expired or invalid", activateCode);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @ApiOperation("Sending email with the code to user for password reset")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Email was successfully sent."),
                    @ApiResponse(code = 500, message = "Save forgot-code error")
            }
    )
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

    @ApiOperation("Reset user password using code & new password")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Password was successfully changed."),
                    @ApiResponse(code = 204, message = "No user available with this email."),
                    @ApiResponse(code = 500, message = "Code is expired, or invalid.")
            }
    )
    @PostMapping("/auth/reset")
    public ResponseEntity<User> resetUserPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        String userEmail = userService.getByForgotPasswordCode(resetPasswordRequest.getUserCode());
        if (Objects.nonNull(userEmail)) {
            User userToSave = userService.findByEmail(userEmail)
                    .orElseThrow(ExceptionMessagesUtil.notFoundUser);
            userToSave.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
            log.info("User's ({}) password was changed.", userEmail);
            return ResponseEntity.ok(userService.saveUser(userToSave));
        } else {
            log.error("Reset password code - {} error.\nReason: Reset code is expired or invalid.", resetPasswordRequest.getUserCode());
            throw ExceptionMessagesUtil.forgotPasswordCodeError.get();
        }
    }

    @ApiOperation("Checking user forgot_pass code passed as part of the request.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "forgot_pass code is OK!"),
                    @ApiResponse(code = 500, message = "forgot_pass code  is invalid or expired.")
            }
    )
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

    @ApiOperation("Logout")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "User successfully logout"),
                    @ApiResponse(code = 500, message = "Logout exception.")
            }
    )
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
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private List<GrantedAuthority> getAuthorities(User user) {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }
}
