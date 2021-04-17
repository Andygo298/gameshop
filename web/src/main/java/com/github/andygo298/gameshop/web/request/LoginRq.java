package com.github.andygo298.gameshop.web.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

public class LoginRq implements Serializable {
    private String email;
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
