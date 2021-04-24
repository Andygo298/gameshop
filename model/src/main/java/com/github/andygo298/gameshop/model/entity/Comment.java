package com.github.andygo298.gameshop.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.andygo298.gameshop.model.entity.jsonUtil.LocalDateDeserializer;
import com.github.andygo298.gameshop.model.entity.jsonUtil.LocalDateSerializer;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@Table(name = "comment")
@Entity
@AllArgsConstructor
@Where(clause="isDelete=false")
public class Comment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer commentId;
    @Column(name = "message", nullable = false)
    private String message;
    @Column(name = "commentMark")
    private int commentMark;
    @Column(name = "created_at", nullable = false, updatable = false)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate createdAt;
    @Column(name = "updated_at")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate updatedAt;
    @Column(name = "isApproved")
    private boolean isApproved;
    @Column(name = "isDelete")
    private boolean isDelete;
    @Column(name = "user_id", nullable = false, insertable = false, updatable = false)
    private Integer userId;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    public Comment() {
        this.commentMark = 0;
    }

    public static class CommentBuilder {

        private Comment newComment;

        public CommentBuilder() {
            newComment = new Comment();
        }

        public Comment.CommentBuilder withMessage(String message) {
            newComment.message = message;
            return this;
        }

        public Comment.CommentBuilder withCommentMark(int commentMark) {
            newComment.commentMark = commentMark;
            return this;
        }

        public Comment.CommentBuilder withCreatedAt(LocalDate createdAt) {
            newComment.createdAt = createdAt;
            return this;
        }

        public Comment.CommentBuilder withUser(User user) {
            newComment.user = user;
            return this;
        }

        public Comment.CommentBuilder withUserId(Integer userId) {
            newComment.userId = userId;
            return this;
        }

        public Comment build() {
            return newComment;
        }
    }
}
