package com.github.andygo298.gameshop.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.andygo298.gameshop.model.enums.Role;
import com.github.andygo298.gameshop.model.enums.Status;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@Table(name = "user")
@AllArgsConstructor
@RequiredArgsConstructor
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer userId;
    @Column(name = "first_name", nullable = false)
    @NonNull
    private String firstName;
    @Column(name = "last_name", nullable = false)
    @NonNull
    private String lastName;
    @Column(name = "password", nullable = false)
    @NonNull
    @JsonIgnore
    private String password;
    @Column(name = "email", nullable = false, unique = true)
    @NonNull
    private String email;
    @Column(name = "mark")
    private Integer mark;
    @Column(name = "created_at", nullable = false, updatable = false)
    @NonNull
    @JsonIgnore
    private LocalDate createdAt;
    @Column(name = "role", nullable = false)
    @NonNull
    @Enumerated(EnumType.STRING)
    private Role role;
    @Column(name = "status", nullable = false)
    @NonNull
    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private Set<Comment> comments = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinTable(name = "user_game",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "game_id")})
    private Set<Game> games = new HashSet<>();

    @Builder(builderMethodName = "builder")
    public static User newUser(String firstName, String lastName, String password, String email, LocalDate createdAt, Role role, Status status) {
        return new User(firstName, lastName, password, email, createdAt, role, status);
    }
}
