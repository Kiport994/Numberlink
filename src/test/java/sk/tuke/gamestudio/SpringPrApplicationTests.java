package sk.tuke.gamestudio;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import sk.tuke.gamestudio.game.NumberLink;
import sk.tuke.gamestudio.game.Logic_NumberLink;
import sk.tuke.gamestudio.game.map.MyMap;
import sk.tuke.gamestudio.service.comment.CommentService;
import sk.tuke.gamestudio.service.rating.RatingService;
import sk.tuke.gamestudio.service.score.ScoreService;

@SpringBootTest
@ComponentScan(
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = "sk\\.tuke\\.gamestudio\\.SpringPrApplication\\$.*runner"
        )
)
@ActiveProfiles("test")
class SpringPrApplicationTests {

    @Autowired
    private MyMap myMap;

    @Autowired
    private Logic_NumberLink logicNumberLink;

    @Autowired
    private NumberLink numberLink;

    @Autowired
    private ScoreService scoreService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private RatingService ratingService;

    @Autowired
    private RestTemplate restTemplate;

    @Test
    void contextLoads() {
        assertNotNull(myMap, "Bean myMap be created");
        assertNotNull(logicNumberLink, "Bean logicNumberLink be created");
        assertNotNull(numberLink, "Bean numberLink be created");
        assertNotNull(scoreService, "Bean scoreService be created");
        assertNotNull(commentService, "Bean commentService be created");
        assertNotNull(ratingService, "Bean ratingService be created");
        assertNotNull(restTemplate, "Bean restTemplate be created");
    }
}
