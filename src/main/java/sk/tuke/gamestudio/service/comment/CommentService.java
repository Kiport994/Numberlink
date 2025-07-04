    package sk.tuke.gamestudio.service.comment;

    import sk.tuke.gamestudio.entity.Comment;

    import java.util.List;

    public interface CommentService {
        void addComment(Comment comment) throws CommentException;
        List<Comment> getLastComment(String game) throws CommentException;
        void resetComment() throws CommentException;
    }
