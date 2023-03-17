package ru.skypro.homework.exception;

public class CommentNotFoundException extends RuntimeException{
    private final int commentId;

    public CommentNotFoundException(int commentId) {
        this.commentId = commentId;
    }

    public int getCommentId() {
        return commentId;
    }
}
