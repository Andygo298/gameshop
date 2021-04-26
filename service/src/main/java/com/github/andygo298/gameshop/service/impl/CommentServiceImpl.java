package com.github.andygo298.gameshop.service.impl;

import com.github.andygo298.gameshop.dao.CommentDao;
import com.github.andygo298.gameshop.dao.UserDao;
import com.github.andygo298.gameshop.model.RatingTraderDto;
import com.github.andygo298.gameshop.model.entity.Comment;
import com.github.andygo298.gameshop.model.entity.User;
import com.github.andygo298.gameshop.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentDao commentDao;
    @Autowired
    private UserDao userDao;

    @Override
    @Transactional
    public Optional<List<Comment>> getCommentsByUserId(Integer userId) {
        return commentDao.getCommentsByUserId(userId);
    }

    @Override
    @Transactional
    public Optional<Comment> getCommentByUserIdAndCommentId(Integer userId, Integer commentId) {
        return commentDao.getCommentByUserIdAndCommentId(userId, commentId);
    }

    @Override
    @Transactional
    public Optional<Comment> saveComment(Comment comment) {
        User userById = userDao.findById(comment.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not Found"));
        comment.setUser(userById);
        return Optional.of(commentDao.save(comment));
    }

    @Override
    @Transactional
    public Optional<Comment> getCommentById(Integer commentId) {
        return commentDao.findById(commentId);
    }

    @Override
    @Transactional
    public Comment updateComment(Comment commentToUpdate) {
        return commentDao.save(commentToUpdate);
    }

    @Override
    @Transactional
    public Comment deleteCommentById(Integer commentId) {
        Comment byId = commentDao.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("commentId not Found"));
        byId.setDelete(true);
        return commentDao.save(byId);
    }

    @Override
    @Transactional
    public int getTotalRatingByUserId(Integer userId) {
        return commentDao.getTotalRatingByUserId(userId);
    }

    @Override
    public List<RatingTraderDto> getTradersRating() {
        return commentDao.getTradersRating();
    }
}
