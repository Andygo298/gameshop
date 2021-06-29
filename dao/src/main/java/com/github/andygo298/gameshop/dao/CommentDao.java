package com.github.andygo298.gameshop.dao;

import com.github.andygo298.gameshop.model.RatingTraderDto;
import com.github.andygo298.gameshop.model.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CommentDao extends JpaRepository<Comment, Integer> {

    Optional<List<Comment>> getCommentsByUserId(Integer userId);

    Optional<Comment> getCommentByUserIdAndCommentId(Integer userId, Integer commentId);

    @Query("select sum(c.commentMark) from Comment c where c.userId=?1")
    int getTotalRatingByUserId(Integer userId);

    @Query(value = "select new com.github.andygo298.gameshop.model.RatingTraderDto(u.email,u.firstName,sum(c.commentMark)) " +
            "from UserEntity as u " +
            "left join Comment as c on u.userId=c.userId " +
            "group by u.userId " +
            "order by sum(c.commentMark) desc")
    List<RatingTraderDto> getTradersRating();
}
