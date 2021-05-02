package com.github.andygo298.gameshop.web.controller.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.function.Function;
import java.util.function.Supplier;

public class ExceptionMessagesUtil {
    private static final Logger log = LoggerFactory.getLogger(ExceptionMessagesUtil.class);

    public static Supplier<ResponseStatusException> unauthorizedError = () -> {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "email or password is invalid");
    };
    public static Supplier<ResponseStatusException> accessDenied = () -> {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied.");
    };
    public static Function<String, ResponseStatusException> userIsNotActivated = (email) -> {
        log.warn("User - {} isn't activated.", email);
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User - " + email + " isn't activated.");
    };
    public static Supplier<ResponseStatusException> activateCodeError = () -> {
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Activate code link is expired, or invalid");
    };
    public static Supplier<ResponseStatusException> forgotPasswordCodeError = () -> {
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Code is expired, or invalid");
    };
    public static Function<String, ResponseStatusException> userExists = (email) -> {
        log.error("User - {} already exists.", email);
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already exists - " + email);
    };
    public static Supplier<ResponseStatusException> notFoundUser = () -> {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No user available with this email.");
    };
    public static Supplier<ResponseStatusException> gameNotFound = () -> {
        log.error("Game id is invalid or game not found");
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game id is invalid or game not found");
    };
    public static Supplier<ResponseStatusException> userNotFound = () -> {
        log.error("User id is invalid or user not found");
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User id is invalid or user not found");
    };
    public static Supplier<ResponseStatusException> userOrCommentsNotFound = () -> {
        log.error("User or(and) comment is invalid or not found");
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User or(and) comment is invalid or not found");
    };
}
