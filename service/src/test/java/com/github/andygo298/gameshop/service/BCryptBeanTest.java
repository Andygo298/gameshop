package com.github.andygo298.gameshop.service;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

public class BCryptBeanTest {

    @Test
    void bCryptTest() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(8);
        final String testPass = "2481066";
        String encodePass = passwordEncoder.encode(testPass);
        assertTrue(passwordEncoder.matches(testPass, encodePass));
    }
}
