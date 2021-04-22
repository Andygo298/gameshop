package com.github.andygo298.gameshop.web.controller;

import com.github.andygo298.gameshop.model.entity.Comment;
import com.github.andygo298.gameshop.service.CommentService;
import com.github.andygo298.gameshop.service.UserService;
import com.github.andygo298.gameshop.web.request.CommentRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@RestController
@CrossOrigin(origins = "http://localhost:80/gameshop")
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;

    public CommentController(CommentService commentService, UserService userService) {
        this.commentService = commentService;
        this.userService = userService;
    }
    Supplier<ResponseStatusException> userNotFound = () -> {
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User id is invalid or user not found");
    };

    @PostMapping("/articles/{id}/comments")
    public ResponseEntity<Comment> saveComment(@PathVariable("id") Integer userId, @RequestBody CommentRequest commentRequest){
        Comment commentToSave = new Comment.CommentBuilder().withMessage(commentRequest.getMessage())
                .withUserId(userId)
                .withCreatedAt(LocalDateTime.now().toLocalDate())
                .build();

        Optional<Comment> commentFromDb = commentService.saveComment(commentToSave, commentRequest.getMark());
        if (commentFromDb.isPresent()){
            return ResponseEntity.status(HttpStatus.OK).body(commentFromDb.get());
        }else {
            throw userNotFound.get();
        }
    }

    @GetMapping("/users/{id}/comments")
    public ResponseEntity<List<Comment>> getAllComments(@PathVariable("id")Integer userId){
        Optional<List<Comment>> commentsByUserId = commentService.getCommentsByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(commentsByUserId.get());
    }

}
