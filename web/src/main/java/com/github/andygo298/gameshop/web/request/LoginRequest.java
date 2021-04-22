package com.github.andygo298.gameshop.web.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
public class LoginRequest implements Serializable {
    private String email;
    private String password;
}
