package com.github.andygo298.gameshop.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class CommentFilter {

    private Integer userId;
    private Integer page;
    private Integer limit;
    private String createdAt;
    private Integer mark;
    private String sort;
    private String order;
}
