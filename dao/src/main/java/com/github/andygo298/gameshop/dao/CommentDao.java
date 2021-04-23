package com.github.andygo298.gameshop.dao;

import com.github.andygo298.gameshop.model.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CommentDao extends JpaRepository<Comment, Integer>{
    Optional<List<Comment>> getCommentsByUserId(Integer userId);
    Optional<Comment> getCommentByUserIdAndCommentId(Integer userId, Integer commentId);
    @Query("select sum(c.commentMark) from Comment c where c.userId=:userId")
    int getTotalRatingByUserId(Integer userId);
}
