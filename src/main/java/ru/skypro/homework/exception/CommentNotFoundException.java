package ru.skypro.homework.exception;
/**
 * Exception - Комментарий не найден
 */
public class CommentNotFoundException extends RuntimeException{
    private final int commentId;

    public CommentNotFoundException(int commentId) {
        this.commentId = commentId;
    }

    public int getCommentId() {
        return commentId;
    }
}
