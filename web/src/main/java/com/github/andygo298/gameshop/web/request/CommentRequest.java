package com.github.andygo298.gameshop.web.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CommentRequest {
    private String message;
    private Integer mark;
}
