package com.github.andygo298.gameshop.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingTraderDto {
    private String email;
    private String firstName;
    private long TraderRating;
}
