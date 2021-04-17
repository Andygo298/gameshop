package com.github.andygo298.gameshop.model.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "game")
@Data
@NoArgsConstructor
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer gameId;
    @Column(name = "game_name", nullable = false)
    private String gameName;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "isDelete")
    private boolean isDelete;
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, mappedBy = "games", fetch = FetchType.EAGER )
    private Set<User> users = new HashSet<>();

}
