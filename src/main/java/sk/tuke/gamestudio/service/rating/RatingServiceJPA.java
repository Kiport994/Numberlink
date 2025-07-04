package sk.tuke.gamestudio.service.rating;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import sk.tuke.gamestudio.entity.Rating;

import java.util.List;

@Repository
@Transactional
public class RatingServiceJPA implements RatingService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void addRating(Rating rating) throws RatingException {
        entityManager.persist(rating);
    }

    @Override
    public List<Rating> getTopRate(String game) throws RatingException {
        return entityManager.createNamedQuery("Rating.getTopRate")
                .setParameter("game", game)
                .setMaxResults(3)
                .getResultList();
    }

    @Override
    public void resetRating() {
        entityManager.createNamedQuery("Rating.resetRating").executeUpdate();
        entityManager.clear();
    }
}
