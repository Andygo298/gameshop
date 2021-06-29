package com.github.andygo298.gameshop.web.controller;

import com.github.andygo298.gameshop.model.entity.UserEntity;
import com.github.andygo298.gameshop.model.enums.Role;
import com.github.andygo298.gameshop.model.enums.Status;
import com.github.andygo298.gameshop.model.jwt.JwtRequest;
import com.github.andygo298.gameshop.model.jwt.JwtResponse;
import com.github.andygo298.gameshop.service.UserService;
import com.github.andygo298.gameshop.service.impl.JwtUserDetailsServiceImpl;
import com.github.andygo298.gameshop.web.config.SwaggerConfig;
import com.github.andygo298.gameshop.web.controller.util.ExceptionMessagesUtil;
import com.github.andygo298.gameshop.web.jwt.JwtTokenUtil;
import com.github.andygo298.gameshop.web.request.ForgotPasswordRequest;
import com.github.andygo298.gameshop.web.request.RegistrationRequest;
import com.github.andygo298.gameshop.web.request.ResetPasswordRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Objects;

@RestController
@RequestMapping("/")
@Api(tags = {SwaggerConfig.TAG_1})
public class UserController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private JwtUserDetailsServiceImpl userDetailsService;

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
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(token));
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
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
    public ResponseEntity<UserEntity> registration(@RequestBody RegistrationRequest registrationRequest) {
        String registrationEmail = registrationRequest.getEmail();
        if (userService.findByEmail(registrationEmail).isPresent()) {
            log.info("User email - {} already exists.", registrationEmail);
            throw ExceptionMessagesUtil.userExists.apply(registrationEmail);
        }
        UserEntity userEntityToSave = UserEntity.builder()
                .firstName(registrationRequest.getFirstName())
                .lastName(registrationRequest.getLastName())
                .email(registrationRequest.getEmail())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .role(Role.valueOf(registrationRequest.getRole().toString()))
                .status(Status.BANNED)
                .createdAt(LocalDateTime.now().toLocalDate())
                .build();
        if (userService.saveActivateCode(userEntityToSave)) {
            UserEntity userEntity = userService.saveUser(userEntityToSave);
            log.info("User - {} was created at : {}", userEntity.getEmail(), LocalDateTime.now().toLocalDate());
            return ResponseEntity.status(HttpStatus.CREATED).body(userEntity);
        } else {
            log.error("User - {} was created at : {}.\nReason: {}", userEntityToSave.getEmail(),
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
            UserEntity userEntity = userService.activateUserByCode(activateCode)
                    .orElseThrow(ExceptionMessagesUtil.activateCodeError);
            userEntity.setStatus(Status.ACTIVE);
            userService.saveUser(userEntity);
            log.info("user's ({}) status was changed to ACTIVE.", userEntity.getEmail());
            return ResponseEntity.ok("User - " + userEntity.getEmail() + " is activated.");
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
    public ResponseEntity<UserEntity> resetUserPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        String userEmail = userService.getByForgotPasswordCode(resetPasswordRequest.getUserCode());
        if (Objects.nonNull(userEmail)) {
            UserEntity userEntityToSave = userService.findByEmail(userEmail)
                    .orElseThrow(ExceptionMessagesUtil.notFoundUser);
            userEntityToSave.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
            log.info("User's ({}) password was changed.", userEmail);
            return ResponseEntity.ok(userService.saveUser(userEntityToSave));
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

}
