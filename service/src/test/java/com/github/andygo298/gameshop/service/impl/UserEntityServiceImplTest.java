package com.github.andygo298.gameshop.service.impl;

import com.github.andygo298.gameshop.dao.UserDao;
import com.github.andygo298.gameshop.model.entity.UserEntity;
import com.github.andygo298.gameshop.model.enums.Role;
import com.github.andygo298.gameshop.model.enums.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.BDDMockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserEntityServiceImplTest {

    @Mock
    private UserDao userDao;
    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveUser() {
        UserEntity userEntity2 = UserEntity.builder()
                .firstName("test2")
                .lastName("testov2")
                .createdAt(LocalDateTime.now().toLocalDate())
                .email("email_2@cao.com")
                .password("asds")
                .role(Role.TRADER)
                .status(Status.ACTIVE)
                .build();
        userEntity2.setUserId(2);
        when(userDao.save(userEntity2)).thenReturn(userEntity2);
        UserEntity userEntity = userService.saveUser(userEntity2);
        assertNotNull(userEntity);
        assertEquals("email_2@cao.com", userEntity.getEmail());
    }


    @Test
    void findByEmailTest() {
        UserEntity userEntity1 = UserEntity.builder()
                .firstName("test1")
                .lastName("testov1")
                .createdAt(LocalDateTime.now().toLocalDate())
                .email("email_1@cao.com")
                .password("asd")
                .role(Role.TRADER)
                .status(Status.ACTIVE)
                .build();
        userEntity1.setUserId(1);
        Optional<UserEntity> xxx = Optional.of(userEntity1);
        when(userDao.getUserByEmail(userEntity1.getEmail())).thenReturn(xxx);
        Optional<UserEntity> byEmail = userService.findByEmail("email_1@cao.com");
        assertTrue(byEmail.isPresent());
        assertEquals("email_1@cao.com", byEmail.get().getEmail());
    }

    @Test
    void findAllByRole() {
        UserEntity userEntity1 = UserEntity.builder()
                .firstName("test1")
                .lastName("testov1")
                .createdAt(LocalDateTime.now().toLocalDate())
                .email("email_1@cao.com")
                .password("asd")
                .role(Role.TRADER)
                .status(Status.ACTIVE)
                .build();
        UserEntity userEntity2 = UserEntity.builder()
                .firstName("test2")
                .lastName("testov2")
                .createdAt(LocalDateTime.now().toLocalDate())
                .email("email_2@cao.com")
                .password("asd")
                .role(Role.TRADER)
                .status(Status.ACTIVE)
                .build();
        ArrayList<UserEntity> userEntities = new ArrayList<>(Arrays.asList(userEntity1, userEntity2));
        when(userDao.findAllByRole(Role.TRADER)).thenReturn(Optional.of(userEntities));
        Optional<List<UserEntity>> allByRole = userService.findAllByRole(Role.TRADER);
        assertTrue(allByRole.isPresent());
        assertEquals(userEntities.size() ,allByRole.get().size());
    }

    @Test
    void getUserById() {
        UserEntity userEntity1 = UserEntity.builder()
                .firstName("test1")
                .lastName("testov1")
                .createdAt(LocalDateTime.now().toLocalDate())
                .email("email_1@cao.com")
                .password("asd")
                .role(Role.TRADER)
                .status(Status.ACTIVE)
                .build();
        userEntity1.setUserId(1);
        given(userDao.findById(userEntity1.getUserId())).willReturn(Optional.of(userEntity1));
        Optional<UserEntity> userById = userService.getUserById(userEntity1.getUserId());
        assertTrue(userById.isPresent());
        assertEquals(userEntity1.getEmail(),userById.get().getEmail());
    }
}