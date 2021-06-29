package com.github.andygo298.gameshop.service.impl;

import com.github.andygo298.gameshop.dao.CommentDao;
import com.github.andygo298.gameshop.dao.UserDao;
import com.github.andygo298.gameshop.model.CommentFilter;
import com.github.andygo298.gameshop.model.RatingTraderDto;
import com.github.andygo298.gameshop.model.entity.Comment;
import com.github.andygo298.gameshop.model.entity.UserEntity;
import com.github.andygo298.gameshop.service.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {

    private static final Logger log = LoggerFactory.getLogger(GameServiceImpl.class);
    @Autowired
    private CommentDao commentDao;
    @Autowired
    private UserDao userDao;

    @Override
    public Optional<List<Comment>> getCommentsByUserId(Integer userId) {
        return commentDao.getCommentsByUserId(userId);
    }

    @Override
    public Optional<Comment> getCommentByUserIdAndCommentId(Integer userId, Integer commentId) {
        return commentDao.getCommentByUserIdAndCommentId(userId, commentId);
    }

    @Override
    public Page<Comment> getCommentWithPagination(CommentFilter commentFilter) {
        Pageable pageable;
        if (commentFilter.getOrder().equals("asc")) {
            pageable = PageRequest.of(commentFilter.getPage(), commentFilter.getLimit(), Sort.by(commentFilter.getSort()).ascending());
        } else {
            pageable = PageRequest.of(commentFilter.getPage(), commentFilter.getLimit(), Sort.by(commentFilter.getSort()).descending());
        }

        if (Objects.nonNull(commentFilter.getCreatedAt())){
            return commentDao.findAll(pageable, commentFilter.getUserId(), LocalDate.parse(commentFilter.getCreatedAt()), commentFilter.getMark());
        }else {
            return commentDao.findAll(pageable, commentFilter.getUserId(), null, commentFilter.getMark());
        }
    }

    @Override
    public Comment saveComment(Comment comment) {
        UserEntity userEntityById = userDao.findById(comment.getUserId())
                .orElseThrow(() -> {
                    log.error("User with id - {} not found.", comment.getUserId());
                    return new EntityNotFoundException("User not Found");
                });
        comment.setUserEntity(userEntityById);
        return commentDao.save(comment);
    }

    @Override
    public Optional<Comment> getCommentById(Integer commentId) {
        return commentDao.findById(commentId);
    }

    @Override
    public Comment updateComment(Comment commentToUpdate) {
        return commentDao.save(commentToUpdate);
    }

    @Override
    public Comment deleteCommentById(Integer commentId) {
        Comment byId = commentDao.findById(commentId)
                .orElseThrow(() -> {
                    log.error("Comment with id - {} not found", commentId);
                    return new EntityNotFoundException("Comment not Found");
                });
        byId.setDelete(true);
        return commentDao.save(byId);
    }

    @Override
    public int getTotalRatingByUserId(Integer userId) {
        return commentDao.getTotalRatingByUserId(userId);
    }

    @Override
    public List<RatingTraderDto> getTradersRating() {
        return commentDao.getTradersRating();
    }
}
