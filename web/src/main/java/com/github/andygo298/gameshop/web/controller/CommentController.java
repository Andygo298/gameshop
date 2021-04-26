package com.github.andygo298.gameshop.web.controller;

import com.github.andygo298.gameshop.model.RatingTraderDto;
import com.github.andygo298.gameshop.model.entity.Comment;
import com.github.andygo298.gameshop.model.entity.User;
import com.github.andygo298.gameshop.model.enums.Role;
import com.github.andygo298.gameshop.service.CommentService;
import com.github.andygo298.gameshop.service.UserService;
import com.github.andygo298.gameshop.web.request.CommentRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@RestController
@RequestMapping("/api")
public class CommentController {

    private static final Logger log = LoggerFactory.getLogger(CommentController.class);
    private final CommentService commentService;
    private final UserService userService;

    public CommentController(CommentService commentService, UserService userService) {
        this.commentService = commentService;
        this.userService = userService;
    }

    private Supplier<ResponseStatusException> userNotFound = () -> {
        log.error("User id is invalid or user not found");
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User id is invalid or user not found");
    };
    private Supplier<ResponseStatusException> userOrCommentsNotFound = () -> {
        log.error("User or(and) comment is invalid or not found");
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User or(and) comment is invalid or not found");
    };

    @PostMapping("/articles/{id}/comments")
    public ResponseEntity<Comment> saveComment(@PathVariable("id") Integer userId, @RequestBody CommentRequest commentRequest) {
        Comment commentToSave = new Comment.CommentBuilder()
                .withMessage(commentRequest.getMessage())
                .withUserId(userId)
                .withCreatedAt(LocalDateTime.now().toLocalDate())
                .withCommentMark(commentRequest.getMark())
                .build();
        Optional<Comment> commentFromDb = commentService.saveComment(commentToSave);
        log.info("Comment with userId - {} was saved.", userId);
        return ResponseEntity.ok(commentFromDb.orElseThrow(userNotFound));
    }

    @GetMapping("/admin/articles/{id}/approveComments")
    public ResponseEntity<Comment> approveComment(@PathVariable("id") Integer commentId) {
        Comment commentById = commentService.getCommentById(commentId)
                .orElseThrow(userOrCommentsNotFound);
        commentById.setApproved(true);
        log.info("Comment id - {} status was changed to APPROVED by admin.", commentById);
        return ResponseEntity.ok(commentService.updateComment(commentById));
    }

    @PutMapping("/articles/{id}/comments")
    public ResponseEntity<Comment> updateCommentById(@PathVariable("id") Integer commentId, @RequestBody CommentRequest commentRequest) {
        Optional<Comment> commentById = commentService.getCommentById(commentId);
        Comment commentToUpdate = commentById.orElseThrow(userOrCommentsNotFound);
        commentToUpdate.setMessage(commentRequest.getMessage());
        commentToUpdate.setCommentMark(commentRequest.getMark());
        commentToUpdate.setUpdatedAt(LocalDateTime.now().toLocalDate());
        Comment updateComment = commentService.updateComment(commentToUpdate);
        log.info("Comment with data:{} was updated.", commentRequest.toString());
        return ResponseEntity.ok(updateComment);
    }

    @GetMapping("/users/rating")
    public ResponseEntity<List<RatingTraderDto>> getTraderRating() {
        return ResponseEntity.ok(commentService.getTradersRating());
    }

    @GetMapping("/users/traders")
    public ResponseEntity<List<User>> getTraders() {
        Optional<List<User>> allByRole = userService.findAllByRole(Role.TRADER);
        return ResponseEntity.ok(allByRole.orElseThrow(userOrCommentsNotFound));
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


    @DeleteMapping("/users/{id}/comments/{id}")
    public ResponseEntity<Comment> deleteComment(Authentication authentication,
                                                 @PathVariable("id") Integer userId, @PathVariable("id") Integer commentId) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        String currentUserEmail = ((User) authentication.getPrincipal()).getEmail();
        Optional<User> byAuthEmail = userService.findByEmail(currentUserEmail);
        if (isAdmin || byAuthEmail.isPresent()) {
            User currentUser = byAuthEmail.orElseThrow(userOrCommentsNotFound);
            if (currentUser.getUserId().equals(userId) || isAdmin) {
                log.info("Comment with id - {} was deleted.", commentId);
                return ResponseEntity.ok(commentService.deleteCommentById(commentId));
            }
        }
        log.error("Delete Error comment with id - {}.\nReason: ",commentId);
        throw userOrCommentsNotFound.get();
    }
}
