package com.github.andygo298.gameshop.dao;

import com.github.andygo298.gameshop.dao.config.DaoConfig;
import com.github.andygo298.gameshop.model.entity.UserEntity;
import com.github.andygo298.gameshop.model.enums.Role;
import com.github.andygo298.gameshop.model.enums.Status;
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

public class UserEntityDaoTest {

    @Autowired
    private UserDao userDao;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(8);

    @Test
    void saveUserTest() {
        UserEntity userEntityToSave = UserEntity.builder()
                .firstName("test")
                .lastName("testov")
                .password(passwordEncoder.encode("test123"))
                .email("test@gmail.com")
                .role(Role.USER)
                .status(Status.ACTIVE)
                .createdAt(LocalDateTime.now().toLocalDate())
                .build();
        UserEntity saveU = userDao.save(userEntityToSave);
        Optional<UserEntity> actualUser = userDao.findById(saveU.getUserId());
        assertTrue(actualUser.isPresent());
        assertEquals(actualUser.get().getEmail(), userEntityToSave.getEmail());
    }

    @Test
    void getUserByEmail() {
        UserEntity userEntityToSave = UserEntity.builder()
                .firstName("test")
                .lastName("testov")
                .password(passwordEncoder.encode("test123"))
                .email("test@gmail.com")
                .role(Role.USER)
                .status(Status.ACTIVE)
                .createdAt(LocalDateTime.now().toLocalDate())
                .build();
        userDao.save(userEntityToSave);
        Optional<UserEntity> actualUserByEmail = userDao.getUserByEmail(userEntityToSave.getEmail());
        assertTrue(actualUserByEmail.isPresent());
        assertEquals(actualUserByEmail.get().getEmail(), userEntityToSave.getEmail());
    }

    @Test
    void findAllByRoleTest() {
        UserEntity trader1 = UserEntity.builder()
                .firstName("test1")
                .lastName("testov1")
                .password(passwordEncoder.encode("test123"))
                .email("test1@gmail.com")
                .role(Role.TRADER)
                .status(Status.ACTIVE)
                .createdAt(LocalDateTime.now().toLocalDate())
                .build();
        UserEntity trader2 = UserEntity.builder()
                .firstName("test2")
                .lastName("testov2")
                .password(passwordEncoder.encode("test123"))
                .email("test2@gmail.com")
                .role(Role.TRADER)
                .status(Status.ACTIVE)
                .createdAt(LocalDateTime.now().toLocalDate())
                .build();
        ArrayList<UserEntity> userEntities = new ArrayList<>(Arrays.asList(trader1, trader2));
        userDao.saveAll(userEntities);
        Optional<List<UserEntity>> allByRole = userDao.findAllByRole(Role.TRADER);
        assertTrue(allByRole.isPresent());
        assertEquals(allByRole.get().size(), userEntities.size());
        assertEquals(allByRole.get().get(0).getFirstName(), trader1.getFirstName());
    }
}
