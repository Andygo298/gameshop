package com.github.andygo298.gameshop.web.request;

import com.github.andygo298.gameshop.model.enums.Role;
import lombok.Data;

@Data
public class RegistrationRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Role role;
}
