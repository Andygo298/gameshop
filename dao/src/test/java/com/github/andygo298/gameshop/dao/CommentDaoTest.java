package com.github.andygo298.gameshop.dao;

import com.github.andygo298.gameshop.dao.config.DaoConfig;
import com.github.andygo298.gameshop.model.RatingTraderDto;
import com.github.andygo298.gameshop.model.entity.Comment;
import com.github.andygo298.gameshop.model.entity.User;
import com.github.andygo298.gameshop.model.enums.Role;
import com.github.andygo298.gameshop.model.enums.Status;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.transaction.AfterTransaction;
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
public class CommentDaoTest {

    @Autowired
    private CommentDao commentDao;
    @Autowired
    UserDao userDao;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(8);

    @Test
    void saveCommentTest() {
        User userToSave = User.builder()
                .firstName("test")
                .lastName("testov")
                .password(passwordEncoder.encode("test123"))
                .email("test@gmail.com")
                .role(Role.USER)
                .status(Status.ACTIVE)
                .createdAt(LocalDateTime.now().toLocalDate())
                .build();
        userDao.saveAndFlush(userToSave);
        Optional<User> byId = userDao.findById(1);
        Comment testComment = new Comment.CommentBuilder()
                .withMessage("TestMessage")
                .withCommentMark(4)
                .withCreatedAt(LocalDateTime.now().toLocalDate())
                .withUser(byId.get())
                .build();
        testComment.setApproved(true);
        commentDao.saveAndFlush(testComment);
        Optional<Comment> actualCommentById = commentDao.findById(1);
        assertTrue(actualCommentById.isPresent());
        assertEquals(actualCommentById.get().getMessage(), testComment.getMessage());
    }

    @Test
    void getCommentByUserIdAndCommentIdTest() {
        User userToSave = User.builder()
                .firstName("test1")
                .lastName("testov1")
                .password(passwordEncoder.encode("test123"))
                .email("test1@gmail.com")
                .role(Role.USER)
                .status(Status.ACTIVE)
                .createdAt(LocalDateTime.now().toLocalDate())
                .build();
        userDao.saveAndFlush(userToSave);
        Optional<User> byId = userDao.findById(1);
        Comment testComment = new Comment.CommentBuilder()
                .withMessage("TestMessage")
                .withCommentMark(4)
                .withCreatedAt(LocalDateTime.now().toLocalDate())
                .withUser(byId.get())
                .build();
        testComment.setApproved(true);
        commentDao.saveAndFlush(testComment);
        Optional<Comment> actualCommentByUserIdAndCommentId = commentDao.getCommentByUserIdAndCommentId(1, 1);
        assertTrue(actualCommentByUserIdAndCommentId.isPresent());
        assertEquals(actualCommentByUserIdAndCommentId.get().getMessage(), testComment.getMessage());
    }

    @Test
    void getCommentsByUserId() {
        User userToSave = User.builder()
                .firstName("test22")
                .lastName("testov22")
                .password(passwordEncoder.encode("test123"))
                .email("test22@gmail.com")
                .role(Role.USER)
                .status(Status.ACTIVE)
                .createdAt(LocalDateTime.now().toLocalDate())
                .build();
        User save = userDao.saveAndFlush(userToSave);
        Optional<User> byId = userDao.findById(save.getUserId());
        Comment testComment1 = new Comment.CommentBuilder()
                .withMessage("TestMessage11")
                .withCommentMark(4)
                .withCreatedAt(LocalDateTime.now().toLocalDate())
                .withUser(byId.get())
                .build();
        testComment1.setApproved(true);
        Comment testComment2 = new Comment.CommentBuilder()
                .withMessage("TestMessage22")
                .withCommentMark(5)
                .withCreatedAt(LocalDateTime.now().toLocalDate())
                .withUser(byId.get())
                .build();
        testComment2.setApproved(true);
        ArrayList<Comment> comments = new ArrayList<>(Arrays.asList(testComment1, testComment2));
        commentDao.saveAll(comments);
        Optional<List<Comment>> commentsByUserId = commentDao.getCommentsByUserId(byId.get().getUserId());
        assertTrue(commentsByUserId.isPresent());
        assertEquals(commentsByUserId.get().size(), comments.size());
        assertEquals(comments.get(1).getMessage(), commentsByUserId.get().get(1).getMessage());
    }

    @Test
    void getTotalRatingByUserId() {
        User userToSave = User.builder()
                .firstName("test4")
                .lastName("testov4")
                .password(passwordEncoder.encode("test123"))
                .email("test4@gmail.com")
                .role(Role.USER)
                .status(Status.ACTIVE)
                .createdAt(LocalDateTime.now().toLocalDate())
                .build();
        User save = userDao.saveAndFlush(userToSave);
        Optional<User> byId = userDao.findById(save.getUserId());
        Comment testComment1 = new Comment.CommentBuilder()
                .withMessage("TestMessage1")
                .withCommentMark(1)
                .withCreatedAt(LocalDateTime.now().toLocalDate())
                .withUser(byId.get())
                .build();
        testComment1.setApproved(true);
        Comment testComment2 = new Comment.CommentBuilder()
                .withMessage("TestMessage2")
                .withCommentMark(2)
                .withCreatedAt(LocalDateTime.now().toLocalDate())
                .withUser(byId.get())
                .build();
        testComment2.setApproved(true);
        int result = testComment1.getCommentMark() + testComment2.getCommentMark();
        ArrayList<Comment> comments = new ArrayList<>(Arrays.asList(testComment1, testComment2));
        commentDao.saveAll(comments);
        int actualTotalRatingByUserId = commentDao.getTotalRatingByUserId(byId.get().getUserId());
        assertEquals(actualTotalRatingByUserId, result);
    }

    @Test
    void getTradersRating() {
        userDao.deleteAll();
        commentDao.deleteAll();
        User userToSave1 = User.builder()
                .firstName("test5")
                .lastName("testov5")
                .password(passwordEncoder.encode("test123"))
                .email("test6@gmail.com")
                .role(Role.USER)
                .status(Status.ACTIVE)
                .createdAt(LocalDateTime.now().toLocalDate())
                .build();
        User userToSave2 = User.builder()
                .firstName("test6")
                .lastName("testov6")
                .password(passwordEncoder.encode("test123"))
                .email("test6@gmail.com")
                .role(Role.USER)
                .status(Status.ACTIVE)
                .createdAt(LocalDateTime.now().toLocalDate())
                .build();
        List<User> users = userDao.saveAll(new ArrayList<>(Arrays.asList(userToSave1, userToSave2)));

        Optional<User> byId1 = userDao.findById(users.get(0).getUserId());
        Optional<User> byId2 = userDao.findById(users.get(1).getUserId());
        Comment testComment1 = new Comment.CommentBuilder()
                .withMessage("TestMessage1")
                .withCommentMark(4)
                .withCreatedAt(LocalDateTime.now().toLocalDate())
                .withUser(byId1.get())
                .build();
        testComment1.setApproved(true);
        Comment testComment2 = new Comment.CommentBuilder()
                .withMessage("TestMessage2")
                .withCommentMark(5)
                .withCreatedAt(LocalDateTime.now().toLocalDate())
                .withUser(byId2.get())
                .build();
        testComment2.setApproved(true);
        commentDao.saveAll(new ArrayList<>(Arrays.asList(testComment1, testComment2)));
        List<RatingTraderDto> tradersRating = commentDao.getTradersRating();
        assertFalse(tradersRating.isEmpty());
        assertEquals(tradersRating.get(1).getFirstName(), userToSave1.getFirstName());
    }

}
