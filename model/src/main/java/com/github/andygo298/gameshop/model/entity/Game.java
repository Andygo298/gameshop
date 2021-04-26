package com.github.andygo298.gameshop.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "game")
@Where(clause = "isDelete=false")
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
    @Column(name = "price", nullable = false)
    private Integer price;
    @Column(name = "isDelete")
    @JsonIgnore
    private boolean isDelete;
    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "games", fetch = FetchType.EAGER )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<User> users;

    public Game() {
        this.users = new HashSet<>();
    }

    public static class GameBuilder{
        private Game newGame;

        public GameBuilder() {
            newGame= new Game();
        }
        public Game.GameBuilder withGameName(String gameName){
            newGame.gameName = gameName;
            return this;
        }
        public Game.GameBuilder withTitle(String gameTitle){
            newGame.title = gameTitle;
            return this;
        }
        public Game.GameBuilder withPrice(Integer price){
            newGame.price = price;
            return this;
        }
        public Game build(){
            return newGame;
        }

    }
}
