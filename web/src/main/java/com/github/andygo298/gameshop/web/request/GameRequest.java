package com.github.andygo298.gameshop.web.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameRequest {
    private String gameName;
    private String title;
    private Integer price;
}
