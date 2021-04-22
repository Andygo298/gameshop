package com.github.andygo298.gameshop.service;

import com.github.andygo298.gameshop.model.entity.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    Optional<List<Comment>> getCommentsByUserId(Integer userId);
    Optional<Comment> getCommentByUserIdAndCommentId(Integer userId, Integer commentId);
    Optional<Comment> saveComment(Comment comment, Integer commentMark);
}
