package com.github.andygo298.gameshop.model.entity;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "game")
@AllArgsConstructor
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
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<User> users;

    public Game() {
        this.users = new HashSet<>();
    }
}
