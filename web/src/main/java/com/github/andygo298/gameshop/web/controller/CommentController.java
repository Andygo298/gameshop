package com.github.andygo298.gameshop.web.controller;

import com.github.andygo298.gameshop.model.RatingTraderDto;
import com.github.andygo298.gameshop.model.entity.Comment;
import com.github.andygo298.gameshop.model.entity.UserEntity;
import com.github.andygo298.gameshop.model.enums.Role;
import com.github.andygo298.gameshop.service.CommentService;
import com.github.andygo298.gameshop.service.UserService;
import com.github.andygo298.gameshop.web.config.SwaggerConfig;
import com.github.andygo298.gameshop.web.controller.util.ExceptionMessagesUtil;
import com.github.andygo298.gameshop.web.request.CommentRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@Api(tags = { SwaggerConfig.TAG_2 })
public class CommentController {

    private static final Logger log = LoggerFactory.getLogger(CommentController.class);
    private final CommentService commentService;
    private final UserService userService;

    public CommentController(CommentService commentService, UserService userService) {
        this.commentService = commentService;
        this.userService = userService;
    }

    @ApiOperation("Creates comment using user ID.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 201, message = "Comment was successfully created."),
                    @ApiResponse(code = 404, message = "User not found.")
            }
    )
    @PostMapping("/articles/{userId}/comments")
    public ResponseEntity<Comment> saveComment(@PathVariable("userId") Integer userId, @RequestBody CommentRequest commentRequest) {
        UserEntity userEntity = userService.getUserById(userId).orElseThrow(ExceptionMessagesUtil.userNotFound);
        Comment commentToSave = new Comment.CommentBuilder()
                .withMessage(commentRequest.getMessage())
                .withUserId(userEntity.getUserId())
                .withCreatedAt(LocalDateTime.now().toLocalDate())
                .withCommentMark(commentRequest.getMark())
                .build();
        Comment commentFromDb = commentService.saveComment(commentToSave);
        log.info("Comment with userId - {} was saved.", userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentFromDb);
    }

    @ApiOperation("Approves comment using comment ID. You should login as ADMIN.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Comment was successfully approved."),
                    @ApiResponse(code = 404, message = "User not found.")

            }
    )
    @GetMapping("/admin/articles/{commentId}/approveComments")
    public ResponseEntity<Comment> approveComment(@PathVariable("commentId") Integer commentId) {
        Comment commentById = commentService.getCommentById(commentId)
                .orElseThrow(ExceptionMessagesUtil.userOrCommentsNotFound);
        commentById.setApproved(true);
        log.info("Comment id - {} status was changed to APPROVED by admin.", commentById);
        return ResponseEntity.ok(commentService.updateComment(commentById));
    }

    @ApiOperation("Updates comment using comment ID.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Comment was successfully updated."),
                    @ApiResponse(code = 404, message = "User or comment not found.")

            }
    )
    @PutMapping("/articles/{commentId}/comments")
    public ResponseEntity<Comment> updateCommentById(@PathVariable("commentId") Integer commentId, @RequestBody CommentRequest commentRequest) {
        Optional<Comment> commentById = commentService.getCommentById(commentId);
        Comment commentToUpdate = commentById.orElseThrow(ExceptionMessagesUtil.userOrCommentsNotFound);
        commentToUpdate.setMessage(commentRequest.getMessage());
        commentToUpdate.setCommentMark(commentRequest.getMark());
        commentToUpdate.setUpdatedAt(LocalDateTime.now().toLocalDate());
        Comment updateComment = commentService.updateComment(commentToUpdate);
        log.info("Comment with data:{} was updated.", commentRequest.toString());
        return ResponseEntity.ok(updateComment);
    }

    @ApiOperation("Retrieves a list with traders rating.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "List with rating was successfully retrieved."),
            }
    )
    @GetMapping("/users/rating")
    public ResponseEntity<List<RatingTraderDto>> getTraderRating() {
        return ResponseEntity.ok(commentService.getTradersRating());
    }

    @ApiOperation("Retrieves a list traders.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "List with traders was successfully retrieved."),
                    @ApiResponse(code = 404, message = "Traders not found.")

            }
    )
    @GetMapping("/users/traders")
    public ResponseEntity<List<UserEntity>> getTraders() {
        Optional<List<UserEntity>> allByRole = userService.findAllByRole(Role.TRADER);
        return ResponseEntity.ok(allByRole.orElseThrow(ExceptionMessagesUtil.userNotFound));
    }

    @ApiOperation("Retrieves user's comments using User ID")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "List with user's comments was successfully retrieved."),
                    @ApiResponse(code = 404, message = "User not found.")

            }
    )
    @GetMapping("/users/{userId}/comments")
    public ResponseEntity<List<Comment>> getAllComments(@PathVariable("userId") Integer userId) {
        Optional<List<Comment>> commentsByUserId = commentService.getCommentsByUserId(userId);
        return ResponseEntity.ok(commentsByUserId.orElseThrow(ExceptionMessagesUtil.userNotFound));
    }

    @ApiOperation("Retrieves user's comment using User ID & comment ID.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "User's comment was successfully retrieved."),
                    @ApiResponse(code = 404, message = "User or comment not found.")

            }
    )
    @GetMapping("/users/{userId}/comments/{commentId}")
    public ResponseEntity<Comment> getAllUsersComments(@PathVariable("userId") Integer userId, @PathVariable("commentId") Integer commentId) {
        Optional<Comment> commentByUserIdAndCommentId = commentService.getCommentByUserIdAndCommentId(userId, commentId);
        return ResponseEntity.ok(commentByUserIdAndCommentId.orElseThrow(ExceptionMessagesUtil.userOrCommentsNotFound));
    }

    @ApiOperation("Deletes user's comment using User ID & comment ID.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "User's comment was successfully delete."),
                    @ApiResponse(code = 404, message = "User or comment not found.")

            }
    )
    @DeleteMapping("/users/{userId}/comments/{commentId}")
    public ResponseEntity<Comment> deleteComment(@PathVariable("userId") Integer userId, @PathVariable("commentId") Integer commentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        String currentUserEmail = ((UserEntity) authentication.getPrincipal()).getEmail();
        Optional<UserEntity> byAuthEmail = userService.findByEmail(currentUserEmail);
        if (isAdmin || byAuthEmail.isPresent()) {
            UserEntity currentUserEntity = byAuthEmail.orElseThrow(ExceptionMessagesUtil.userOrCommentsNotFound);
            if (currentUserEntity.getUserId().equals(userId) || isAdmin) {
                log.info("Comment with id - {} was deleted.", commentId);
                return ResponseEntity.ok(commentService.deleteCommentById(commentId));
            }else {
                throw ExceptionMessagesUtil.accessDenied.get();
            }
        }
        log.error("Error delete comment with id - {}.\nReason: Access denied.",commentId);
        throw ExceptionMessagesUtil.userOrCommentsNotFound.get();
    }
}
