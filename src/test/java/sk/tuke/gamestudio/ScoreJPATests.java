    package sk.tuke.gamestudio;

    import static org.junit.jupiter.api.Assertions.*;

    import jakarta.persistence.EntityManager;
    import jakarta.transaction.Transactional;
    import org.junit.jupiter.api.Test;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
    import org.springframework.test.context.ActiveProfiles;
    import sk.tuke.gamestudio.entity.Score;
    import sk.tuke.gamestudio.service.score.ScoreException;
    import sk.tuke.gamestudio.service.score.ScoreServiceJPA;

    import java.util.Date;
    import java.util.List;

    @DataJpaTest
    @Transactional
    @ActiveProfiles("test")
    class ScoreJPATests {

        @Autowired
        private ScoreServiceJPA scoreService;

        @Autowired
        private EntityManager entityManager;

        @Test
        void testAddScoreAndGetTopScores() throws ScoreException {
            Score score1 = new Score("easy", "kiport", 100, new Date());
            Score score2 = new Score("easy", "kiport", 150, new Date());
            Score score3 = new Score("easy", "kiport", 50, new Date());

            scoreService.addScore(score1);
            scoreService.addScore(score2);
            scoreService.addScore(score3);

            entityManager.flush();

            List<Score> topScores = scoreService.getTopScores();

            assertNotNull(topScores, "Musnt be null");
            assertEquals(3, topScores.size(), "Must be 3");

            assertEquals(150, topScores.get(0).getPoints(), "First result 150 points");
            assertEquals(100, topScores.get(1).getPoints(), "Second result 100 points");
            assertEquals(50, topScores.get(2).getPoints(), "Third result 50 points");
        }

        @Test
        void testResetScores() throws ScoreException {
            Score score = new Score("easy", "kiport", 100, new Date());
            scoreService.addScore(score);

            List<Score> topScoresBeforeReset = scoreService.getTopScores();
            assertFalse(topScoresBeforeReset.isEmpty(), "Mustnt be empty");

            scoreService.resetScores();

            List<Score> topScoresAfterReset = scoreService.getTopScores();
            assertTrue(topScoresAfterReset.isEmpty(), "Mustnt be empty");
        }
    }

