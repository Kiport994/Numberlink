package sk.tuke.gamestudio;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import sk.tuke.gamestudio.entity.Comment;
import sk.tuke.gamestudio.service.comment.CommentException;
import sk.tuke.gamestudio.service.comment.CommentServiceJPA;

import java.util.Date;
import java.util.List;

@DataJpaTest
@Transactional
@ActiveProfiles("test")
class CommentJPATests {

    @Autowired
    private CommentServiceJPA commentService;

    @Autowired
    private EntityManager entityManager;

    @Test
    void testAddCommentAndGetLastComment() throws CommentException {
        Comment comment1 = new Comment("kiport", "easy", "First comm", new Date(System.currentTimeMillis() - 10000));
        Comment comment2 = new Comment("kiport1", "easy", "Second comm", new Date(System.currentTimeMillis() - 5000));
        Comment comment3 = new Comment("kiport2", "easy", "Third comm", new Date());

        commentService.addComment(comment1);
        commentService.addComment(comment2);
        commentService.addComment(comment3);

        entityManager.flush();

        List<Comment> lastComments = commentService.getLastComment("easy");

        assertNotNull(lastComments, "Cant be null");
        assertEquals(1, lastComments.size(), "Must be one comment");

        Comment lastComment = lastComments.get(0);
        assertEquals("Third comm", lastComment.getComment(), "Comm should be Third comm");
    }

    @Test
    void testResetComment() throws CommentException {
        Comment comment = new Comment("easy", "kiport", "To rollback", new Date());
        commentService.addComment(comment);
        entityManager.flush();

        List<Comment> commentsBeforeReset = commentService.getLastComment("easy");
        assertTrue(commentsBeforeReset.isEmpty(), "List mustnt be empty");

        commentService.resetComment();
        entityManager.clear();

        List<Comment> commentsAfterReset = commentService.getLastComment("easy");
        assertTrue(commentsAfterReset.isEmpty(), "List must be empty");
    }
}
