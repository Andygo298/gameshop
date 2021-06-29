package com.github.andygo298.gameshop.service;

import com.github.andygo298.gameshop.model.CommentFilter;
import com.github.andygo298.gameshop.model.RatingTraderDto;
import com.github.andygo298.gameshop.model.entity.Comment;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    Optional<List<Comment>> getCommentsByUserId(Integer userId);
    Optional<Comment> getCommentByUserIdAndCommentId(Integer userId, Integer commentId);
    Comment saveComment(Comment comment);
    Optional<Comment> getCommentById(Integer commentId);
    Comment updateComment(Comment commentToUpdate);
    int getTotalRatingByUserId(Integer userId);
    Comment deleteCommentById(Integer commentId);
    List<RatingTraderDto> getTradersRating();
    Page<Comment> getCommentWithPagination(CommentFilter commentFilter);
}
