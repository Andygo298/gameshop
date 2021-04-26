package com.github.andygo298.gameshop.dao;

import com.github.andygo298.gameshop.dao.config.DaoConfig;
import com.github.andygo298.gameshop.model.RatingTraderDto;
import com.github.andygo298.gameshop.model.entity.Comment;
import com.github.andygo298.gameshop.model.entity.Game;
import com.github.andygo298.gameshop.model.entity.User;
import com.github.andygo298.gameshop.model.enums.Role;
import com.github.andygo298.gameshop.model.enums.Status;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DaoConfig.class)
@Transactional
public class TestCommentDaoImpl {

    @Autowired
    private UserDao userDao;
    @Autowired
    private CommentDao commentDao;
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private GameDao gameDao;

    @Test
    void test(){
        User user = User.builder().firstName("test")
                .lastName("testov")
                .email("test@gmail.com")
                .password("test123")
                .role(Role.USER)
                .status(Status.ACTIVE)
                .createdAt(LocalDateTime.now().toLocalDate())
                .build();
        userDao.save(user);

        Comment comment = new Comment();
        comment.setMessage("KYKY");
        comment.setApproved(false);
        comment.setUserId(user.getUserId());
//        comment.setUser(user);
        comment.setCreatedAt(LocalDateTime.now().toLocalDate());
        commentDao.save(comment);
        Comment comment1 = sessionFactory.getCurrentSession().get(Comment.class, 1);

    }

    @Test
    void test2(){
        Optional<User> byId = userDao.findById(1);
        User user = byId.get();
    }

    @Test
    void testGetRating(){
        List<RatingTraderDto> rating = commentDao.getTradersRating();
    }
    @Test
    void testGame(){
        Optional<User> byId = userDao.findById(3);
        User user = byId.get();
        Game game = new Game();
        game.setGameName("testgame");
        game.setTitle("title game test");
        game.setPrice(250);
        game.setDelete(false);
        game.getUsers().add(user);
        Game save = gameDao.save(game);
        user.getGames().add(save);
        userDao.save(user);
    }
}
