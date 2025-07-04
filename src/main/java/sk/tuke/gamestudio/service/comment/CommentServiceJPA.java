package sk.tuke.gamestudio.service.comment;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import sk.tuke.gamestudio.entity.Comment;

import java.util.List;

@Repository
@Transactional
public class CommentServiceJPA implements CommentService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void addComment(Comment comment) throws CommentException {
        entityManager.persist(comment);
    }

    @Override
    public List getLastComment(String game) throws CommentException {
        return entityManager.createNamedQuery("Comment.getLastComment")
                .setParameter("game", game)
                .setMaxResults(1)
                .getResultList();
    }

    @Override
    public void resetComment() {
        entityManager.createNamedQuery("Comment.resetComment").executeUpdate();
        entityManager.clear();
    }
}
