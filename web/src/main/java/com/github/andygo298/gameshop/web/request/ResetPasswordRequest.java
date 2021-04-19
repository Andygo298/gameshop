package com.github.andygo298.gameshop.web.request;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String userCode;
    private String newPassword;
}
