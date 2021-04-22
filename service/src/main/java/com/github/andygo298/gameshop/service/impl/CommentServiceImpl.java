package com.github.andygo298.gameshop.service.impl;

import com.github.andygo298.gameshop.dao.CommentDao;
import com.github.andygo298.gameshop.dao.UserDao;
import com.github.andygo298.gameshop.model.entity.Comment;
import com.github.andygo298.gameshop.model.entity.User;
import com.github.andygo298.gameshop.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Optional<Comment> saveComment(Comment comment, Integer commentMark){
        Optional<User> byId = userDao.findById(comment.getUserId());
        if (byId.isPresent()){
            User user = byId.get();
            Integer newMark = user.getMark() + commentMark;
//            user.setMark(newMark);
            comment.setUser(user);
            return Optional.of(commentDao.save(comment)) ;
        }else{
            return Optional.empty();
        }
    }

}
