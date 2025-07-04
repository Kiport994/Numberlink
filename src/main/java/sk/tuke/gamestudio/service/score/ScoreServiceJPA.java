package sk.tuke.gamestudio.service.score;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import sk.tuke.gamestudio.entity.Score;

import java.util.List;

@Repository
@Transactional
public class ScoreServiceJPA implements ScoreService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void addScore(Score score) throws ScoreException {
        entityManager.persist(score);
    }

    @Override
    public List<Score> getTopScores() throws ScoreException {
        return entityManager.createQuery(
                "SELECT s FROM Score s ORDER BY s.points ASC", Score.class)
                .setMaxResults(16)
                .getResultList();
    }


    @Override
    public void resetScores() {
        entityManager.createNamedQuery("Score.resetScores").executeUpdate();
        entityManager.clear();
    }
}
