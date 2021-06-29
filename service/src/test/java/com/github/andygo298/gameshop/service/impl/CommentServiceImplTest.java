package com.github.andygo298.gameshop.service.impl;

import com.github.andygo298.gameshop.dao.CommentDao;
import com.github.andygo298.gameshop.dao.UserDao;
import com.github.andygo298.gameshop.model.RatingTraderDto;
import com.github.andygo298.gameshop.model.entity.Comment;
import com.github.andygo298.gameshop.model.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.BDDMockito.*;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentDao commentDao;
    @Mock
    private UserDao userDao;
    @InjectMocks
    private CommentServiceImpl commentService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getCommentsByUserIdTest() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(1);
        Comment testComment = new Comment.CommentBuilder().withMessage("test Message")
                .withCreatedAt(LocalDateTime.now().toLocalDate())
                .withCommentMark(4)
                .withUser(userEntity)
                .withUserId(userEntity.getUserId())
                .build();
        Optional<List<Comment>> comments = Optional.of(Collections.singletonList(testComment));
        when(commentDao.getCommentsByUserId(userEntity.getUserId())).thenReturn(comments);
        Optional<List<Comment>> actualCommentsByUserId = commentService.getCommentsByUserId(userEntity.getUserId());
        assertTrue(actualCommentsByUserId.isPresent());
        assertEquals(testComment.getMessage(), actualCommentsByUserId.get().get(0).getMessage());
    }

    @Test
    void getCommentByUserIdAndCommentIdTest() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(1);
        Comment testComment = new Comment.CommentBuilder().withMessage("test Message")
                .withCreatedAt(LocalDateTime.now().toLocalDate())
                .withCommentMark(4)
                .withUser(userEntity)
                .withUserId(userEntity.getUserId())
                .build();
        testComment.setCommentId(1);
        when(commentDao.getCommentByUserIdAndCommentId(testComment.getCommentId(), testComment.getUserId()))
                .thenReturn(Optional.of(testComment));
        Optional<Comment> actualCommentByUserIdAndCommentId = commentService.
                getCommentByUserIdAndCommentId(testComment.getUserId(), testComment.getCommentId());
        assertTrue(actualCommentByUserIdAndCommentId.isPresent());
        assertEquals(testComment.getMessage(), actualCommentByUserIdAndCommentId.get().getMessage());
    }

    @Test
    void saveCommentTest() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(1);
        Comment testComment = new Comment.CommentBuilder().withMessage("test Message")
                .withCreatedAt(LocalDateTime.now().toLocalDate())
                .withCommentMark(4)
                .withUser(userEntity)
                .withUserId(userEntity.getUserId())
                .build();
        given(userDao.findById(userEntity.getUserId())).willReturn(Optional.of(userEntity));
        when(commentDao.save(testComment)).thenReturn(testComment);
        Comment actualComment = commentService.saveComment(testComment);
        assertNotNull(actualComment);
        assertEquals(testComment.getMessage(), actualComment.getMessage());
    }

    @Test
    void saveCommentFailTest() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(1);
        Comment testComment = new Comment.CommentBuilder().withMessage("test Message")
                .withCreatedAt(LocalDateTime.now().toLocalDate())
                .withCommentMark(4)
                .withUser(userEntity)
                .withUserId(userEntity.getUserId())
                .build();
        when(userDao.findById(anyInt())).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> {
            commentService.saveComment(testComment);
        });
    }

    @Test
    void getCommentByIdTest() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(1);
        Comment testComment = new Comment.CommentBuilder().withMessage("test Message")
                .withCreatedAt(LocalDateTime.now().toLocalDate())
                .withCommentMark(4)
                .withUser(userEntity)
                .withUserId(userEntity.getUserId())
                .build();
        testComment.setCommentId(1);
        when(commentDao.findById(testComment.getCommentId())).thenReturn(Optional.of(testComment));
        Optional<Comment> actualCommentById = commentService.getCommentById(testComment.getUserId());
        assertTrue(actualCommentById.isPresent());
        assertEquals(testComment.getMessage(), actualCommentById.get().getMessage());
    }

    @Test
    void updateCommentTest() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(1);
        Comment testUpdateComment = new Comment.CommentBuilder().withMessage("test update Message")
                .withCommentMark(2)
                .withUser(userEntity)
                .withUserId(userEntity.getUserId())
                .build();
        when(commentDao.save(testUpdateComment)).thenReturn(testUpdateComment);
        Comment comment = commentService.updateComment(testUpdateComment);
        assertNotNull(comment);
        assertEquals(testUpdateComment.getMessage(), comment.getMessage());
    }

    @Test
    void deleteCommentByIdTest() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(1);
        Comment testComment = new Comment.CommentBuilder().withMessage("test Message")
                .withCreatedAt(LocalDateTime.now().toLocalDate())
                .withCommentMark(4)
                .withUser(userEntity)
                .withUserId(userEntity.getUserId())
                .build();
        when(commentDao.findById(testComment.getCommentId())).thenReturn(Optional.of(testComment));
        Comment testDelComment = new Comment.CommentBuilder().withMessage("test Message")
                .withCreatedAt(LocalDateTime.now().toLocalDate())
                .withCommentMark(4)
                .withUser(userEntity)
                .withUserId(userEntity.getUserId())
                .build();
        testDelComment.setDelete(true);
        when(commentDao.save(testComment)).thenReturn(testDelComment);
        Comment comment = commentService.deleteCommentById(testComment.getCommentId());
        assertNotNull(comment);
        assertEquals(testDelComment.isDelete(), comment.isDelete());
    }

    @Test
    void getTotalRatingByUserIdTest() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(1);
        int totalRating = 45;
        when(commentDao.getTotalRatingByUserId(userEntity.getUserId())).thenReturn(totalRating);
        int actualRating = commentService.getTotalRatingByUserId(userEntity.getUserId());
        assertEquals(totalRating, actualRating);
    }

    @Test
    void getTradersRatingTest() {
        RatingTraderDto user1 = new RatingTraderDto();
        user1.setFirstName("user1");
        user1.setEmail("email1@gamil.com");
        user1.setTraderRating(50L);
        RatingTraderDto user2 = new RatingTraderDto();
        user2.setFirstName("user2");
        user2.setEmail("email2@gamil.com");
        user2.setTraderRating(35L);
        List<RatingTraderDto> ratingList = new ArrayList<>(Arrays.asList(user1, user2));
        when(commentDao.getTradersRating()).thenReturn(ratingList);
        List<RatingTraderDto> actualTradersRating = commentService.getTradersRating();
        assertFalse(actualTradersRating.isEmpty());
        assertEquals(user1.getTraderRating(),actualTradersRating.get(0).getTraderRating());
    }
}