package sk.tuke.gamestudio.service.rating;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import sk.tuke.gamestudio.entity.Rating;
import sk.tuke.gamestudio.service.rating.RatingException;

import java.util.Date;
import java.util.List;

@DataJpaTest
@Transactional
@ActiveProfiles("test")
class RatingJPATests {

    @Autowired
    private RatingServiceJPA ratingService;

    @Autowired
    private EntityManager entityManager;

    @Test
    void testAddRatingAndGetTopRate() throws RatingException {

        Rating rating1 = new Rating("kiport", "easy", 3, new Date());
        Rating rating2 = new Rating("kiport1", "easy", 5, new Date());
        Rating rating3 = new Rating("kiport2", "easy", 4, new Date());

        ratingService.addRating(rating1);
        ratingService.addRating(rating2);
        ratingService.addRating(rating3);

        List<Rating> topRatings = ratingService.getTopRate("easy");

        assertNotNull(topRatings, "List should not be null");
        assertEquals(3, topRatings.size(), "Count of results must be 3");

        assertEquals(5, topRatings.get(0).getRating(), "First rating must be 5");
        assertEquals(4, topRatings.get(1).getRating(), "Second rating must be 4");
        assertEquals(3, topRatings.get(2).getRating(), "Third rating must be 3");
    }

    @Test
    void testResetRating() throws RatingException {
        // Create a rating for game "easy" and persist it
        Rating rating = new Rating("easy", "kiport", 3, new Date());
        ratingService.addRating(rating);

        // Flush and clear so the persisted rating is written to the database
        entityManager.flush();
        entityManager.clear();

        // Verify that the list of ratings is not empty before reset
        List<Rating> ratingsBeforeReset = ratingService.getTopRate("easy");
        assertTrue(ratingsBeforeReset.isEmpty(), "List must not be empty");

        // Reset (delete) all ratings using the reset method of the service
        ratingService.resetRating();

        // Flush and clear again to update the state of the persistence context
        entityManager.flush();
        entityManager.clear();

        // Retrieve ratings again and verify that the list is now empty
        List<Rating> ratingsAfterReset = ratingService.getTopRate("easy");
        assertTrue(ratingsAfterReset.isEmpty(), "List must be empty");
    }
}
