package com.github.andygo298.gameshop.dao;

import com.github.andygo298.gameshop.dao.config.DaoConfig;
import com.github.andygo298.gameshop.model.entity.User;
import com.github.andygo298.gameshop.model.enums.Role;
import com.github.andygo298.gameshop.model.enums.Status;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DaoConfig.class)
@Transactional
@Rollback

public class UserDaoTest {

    @Autowired
    private UserDao userDao;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(8);

    @Test
    void saveUserTest() {
        User userToSave = User.builder()
                .firstName("test")
                .lastName("testov")
                .password(passwordEncoder.encode("test123"))
                .email("test@gmail.com")
                .role(Role.USER)
                .status(Status.ACTIVE)
                .createdAt(LocalDateTime.now().toLocalDate())
                .build();
        User saveU = userDao.save(userToSave);
        Optional<User> actualUser = userDao.findById(saveU.getUserId());
        assertTrue(actualUser.isPresent());
        assertEquals(actualUser.get().getEmail(), userToSave.getEmail());
    }

    @Test
    void getUserByEmail() {
        User userToSave = User.builder()
                .firstName("test")
                .lastName("testov")
                .password(passwordEncoder.encode("test123"))
                .email("test@gmail.com")
                .role(Role.USER)
                .status(Status.ACTIVE)
                .createdAt(LocalDateTime.now().toLocalDate())
                .build();
        userDao.save(userToSave);
        Optional<User> actualUserByEmail = userDao.getUserByEmail(userToSave.getEmail());
        assertTrue(actualUserByEmail.isPresent());
        assertEquals(actualUserByEmail.get().getEmail(), userToSave.getEmail());
    }

    @Test
    void findAllByRoleTest() {
        User trader1 = User.builder()
                .firstName("test1")
                .lastName("testov1")
                .password(passwordEncoder.encode("test123"))
                .email("test1@gmail.com")
                .role(Role.TRADER)
                .status(Status.ACTIVE)
                .createdAt(LocalDateTime.now().toLocalDate())
                .build();
        User trader2 = User.builder()
                .firstName("test2")
                .lastName("testov2")
                .password(passwordEncoder.encode("test123"))
                .email("test2@gmail.com")
                .role(Role.TRADER)
                .status(Status.ACTIVE)
                .createdAt(LocalDateTime.now().toLocalDate())
                .build();
        ArrayList<User> users = new ArrayList<>(Arrays.asList(trader1, trader2));
        userDao.saveAll(users);
        Optional<List<User>> allByRole = userDao.findAllByRole(Role.TRADER);
        assertTrue(allByRole.isPresent());
        assertEquals(allByRole.get().size(), users.size());
        assertEquals(allByRole.get().get(0).getFirstName(), trader1.getFirstName());
    }
}
