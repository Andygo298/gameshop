package com.github.andygo298.gameshop.web.controller;

import com.github.andygo298.gameshop.model.entity.User;
import com.github.andygo298.gameshop.service.UserService;
import com.github.andygo298.gameshop.web.request.LoginRq;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@RestController
@CrossOrigin(origins = "http://localhost:80/gameshop")
@RequestMapping("/test")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    Supplier<ResponseStatusException> unauthorizedError = () -> {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "email or password is invalid");
    };

    @PostMapping(value = "/login")
    public ResponseEntity<User> login(@RequestBody LoginRq loginRq) {
        String email = loginRq.getEmail();
        String password = loginRq.getPassword();
        Optional<User> optionalUserByLogin = userService.login(loginRq.getEmail(), loginRq.getPassword());
        if (optionalUserByLogin.isPresent()){
            User user = optionalUserByLogin.get();
            Authentication auth = new UsernamePasswordAuthenticationToken(user, null, getAuthorities(user));
            SecurityContextHolder.getContext().setAuthentication(auth);
            return new ResponseEntity<>(user, HttpStatus.OK);
        }else throw unauthorizedError.get();
    }

    private List<GrantedAuthority> getAuthorities(User user) {
        return Collections.singletonList((GrantedAuthority) () -> "ROLE_" + user.getRole().name());
    }

}
