package com.github.andygo298.gameshop.model.jwt;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
public class JwtRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    private String username;
    @Getter
    @Setter
    private String password;

    public JwtRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
