package com.github.andygo298.gameshop.web.controller;

import com.github.andygo298.gameshop.model.entity.Comment;
import com.github.andygo298.gameshop.model.entity.User;
import com.github.andygo298.gameshop.model.enums.Role;
import com.github.andygo298.gameshop.service.CommentService;
import com.github.andygo298.gameshop.service.UserService;
import com.github.andygo298.gameshop.web.request.CommentRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
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
    Supplier<ResponseStatusException> userOrCommentsNotFound = () -> {
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User or(and) comment is invalid or not found");
    };

    @PostMapping("/articles/{id}/comments")
    public ResponseEntity<Comment> saveComment(@PathVariable("id") Integer userId, @RequestBody CommentRequest commentRequest) {
        Comment commentToSave = new Comment.CommentBuilder().withMessage(commentRequest.getMessage())
                .withUserId(userId)
                .withCreatedAt(LocalDateTime.now().toLocalDate())
                .build();

        Optional<Comment> commentFromDb = commentService.saveComment(commentToSave, commentRequest.getMark());
        return ResponseEntity.ok(commentFromDb.orElseThrow(userNotFound));
    }

    @GetMapping("/users/{id}/comments")
    public ResponseEntity<List<Comment>> getAllComments(@PathVariable("id") Integer userId) {
        Optional<List<Comment>> commentsByUserId = commentService.getCommentsByUserId(userId);
        return ResponseEntity.ok(commentsByUserId.orElseThrow(userNotFound));
    }

    @GetMapping("/users/{id}/comments/{id}")
    public ResponseEntity<Comment> getAllUsersComments(@PathVariable("id") Integer userId, @PathVariable("id") Integer commentId) {
        Optional<Comment> commentByUserIdAndCommentId = commentService.getCommentByUserIdAndCommentId(userId, commentId);
        return ResponseEntity.ok(commentByUserIdAndCommentId.orElseThrow(userOrCommentsNotFound));
    }

    @PutMapping("/articles/{id}/comments")
    public ResponseEntity<Comment> updateCommentById(@PathVariable("id") Integer commentId, @RequestBody CommentRequest commentRequest) {
        Optional<Comment> commentById = commentService.getCommentById(commentId);
        Comment commentToUpdate = commentById.orElseThrow(userOrCommentsNotFound);
        commentToUpdate.setMessage(commentRequest.getMessage());
        Comment updateComment = commentService.updateComment(commentToUpdate, commentRequest.getMark());
        return ResponseEntity.ok(updateComment);
    }

    @DeleteMapping("/users/{id}/comments/{id}")
    public ResponseEntity<Comment> deleteComment(Authentication authentication, @PathVariable("id") Integer userId, @PathVariable("id") Integer commentId) {
        if (Objects.nonNull(authentication)){
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));
            String authName = authentication.getName();
            Optional<User> byAuthEmail = userService.findByEmail(authName);
            if (isAdmin || byAuthEmail.isPresent()) {
                User currentUser = byAuthEmail.orElseThrow(userOrCommentsNotFound);
                if (currentUser.getUserId().equals(userId) || isAdmin){
                    return ResponseEntity.ok(commentService.deleteCommentById(commentId));
                }
            }
            throw userOrCommentsNotFound.get();
        }else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

}
