package com.github.andygo298.gameshop.service.impl;

import com.github.andygo298.gameshop.dao.UserDao;
import com.github.andygo298.gameshop.model.entity.User;
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
class UserServiceImplTest {

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
        User user2 = User.builder()
                .firstName("test2")
                .lastName("testov2")
                .createdAt(LocalDateTime.now().toLocalDate())
                .email("email_2@cao.com")
                .password("asds")
                .role(Role.TRADER)
                .status(Status.ACTIVE)
                .build();
        user2.setUserId(2);
        when(userDao.save(user2)).thenReturn(user2);
        User user = userService.saveUser(user2);
        assertNotNull(user);
        assertEquals("email_2@cao.com",user.getEmail());
    }


    @Test
    void findByEmailTest() {
        User user1 = User.builder()
                .firstName("test1")
                .lastName("testov1")
                .createdAt(LocalDateTime.now().toLocalDate())
                .email("email_1@cao.com")
                .password("asd")
                .role(Role.TRADER)
                .status(Status.ACTIVE)
                .build();
        user1.setUserId(1);
        Optional<User> xxx = Optional.of(user1);
        when(userDao.getUserByEmail(user1.getEmail())).thenReturn(xxx);
        Optional<User> byEmail = userService.findByEmail("email_1@cao.com");
        assertTrue(byEmail.isPresent());
        assertEquals("email_1@cao.com", byEmail.get().getEmail());
    }

    @Test
    void findAllByRole() {
        User user1 = User.builder()
                .firstName("test1")
                .lastName("testov1")
                .createdAt(LocalDateTime.now().toLocalDate())
                .email("email_1@cao.com")
                .password("asd")
                .role(Role.TRADER)
                .status(Status.ACTIVE)
                .build();
        User user2 = User.builder()
                .firstName("test2")
                .lastName("testov2")
                .createdAt(LocalDateTime.now().toLocalDate())
                .email("email_2@cao.com")
                .password("asd")
                .role(Role.TRADER)
                .status(Status.ACTIVE)
                .build();
        ArrayList<User> users = new ArrayList<>(Arrays.asList(user1, user2));
        when(userDao.findAllByRole(Role.TRADER)).thenReturn(Optional.of(users));
        Optional<List<User>> allByRole = userService.findAllByRole(Role.TRADER);
        assertTrue(allByRole.isPresent());
        assertEquals(users.size() ,allByRole.get().size());
    }

    @Test
    void getUserById() {
        User user1 = User.builder()
                .firstName("test1")
                .lastName("testov1")
                .createdAt(LocalDateTime.now().toLocalDate())
                .email("email_1@cao.com")
                .password("asd")
                .role(Role.TRADER)
                .status(Status.ACTIVE)
                .build();
        user1.setUserId(1);
        given(userDao.findById(user1.getUserId())).willReturn(Optional.of(user1));
        Optional<User> userById = userService.getUserById(user1.getUserId());
        assertTrue(userById.isPresent());
        assertEquals(user1.getEmail(),userById.get().getEmail());
    }
}