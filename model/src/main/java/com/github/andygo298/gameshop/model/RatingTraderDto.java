package com.github.andygo298.gameshop.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
public class RatingTraderDto implements Serializable {
    private String email;
    private String firstName;
    private Long traderRating;

    public RatingTraderDto(String email, String firstName, Long traderRating) {
        this.email = email;
        this.firstName = firstName;
        if (Objects.nonNull(traderRating)){
            this.traderRating = traderRating;
        }else {
            this.traderRating = 0L;
        }
    }
}
