package com.github.andygo298.gameshop.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.andygo298.gameshop.model.entity.jsonUtil.LocalDateDeserializer;
import com.github.andygo298.gameshop.model.entity.jsonUtil.LocalDateSerializer;
import com.github.andygo298.gameshop.model.enums.Role;
import com.github.andygo298.gameshop.model.enums.Status;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "user")
@AllArgsConstructor
@RequiredArgsConstructor
public class UserEntity implements Serializable {

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
    @Column(name = "created_at", nullable = false, updatable = false)
    @NonNull
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate createdAt;
    @Column(name = "role", nullable = false)
    @NonNull
    @Enumerated(EnumType.STRING)
    private Role role;
    @Column(name = "status", nullable = false)
    @NonNull
    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "userEntity", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Set<Comment> comments;
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinTable(name = "user_game",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "game_id")})
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Set<Game> games;

    public UserEntity() {
        this.comments = new HashSet<>();
        this.games = new HashSet<>();
    }

    @Builder(builderMethodName = "builder")
    public static UserEntity newUser(String firstName, String lastName, String password, String email, LocalDate createdAt, Role role, Status status) {
        return new UserEntity(firstName, lastName, password, email, createdAt, role, status);
    }
}
