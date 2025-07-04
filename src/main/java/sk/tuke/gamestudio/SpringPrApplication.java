package sk.tuke.gamestudio;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;
import sk.tuke.gamestudio.game.Logic_NumberLink;
import sk.tuke.gamestudio.game.NumberLink;
import sk.tuke.gamestudio.game.map.MyMap;
import sk.tuke.gamestudio.service.comment.CommentService;
import sk.tuke.gamestudio.service.comment.CommentServiceRestClient;
import sk.tuke.gamestudio.service.rating.RatingService;
import sk.tuke.gamestudio.service.rating.RatingServiceRestClient;
import sk.tuke.gamestudio.service.score.ScoreService;
import sk.tuke.gamestudio.service.score.ScoreServiceRestClient;

@SpringBootApplication
@ComponentScan(
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = "sk\\.tuke\\.gamestudio\\.server\\..*"
        )
)
public class SpringPrApplication{

    public static void main(String[] args) {
        new SpringApplicationBuilder(SpringPrApplication.class).web(WebApplicationType.NONE).run(args);
    }

    @Bean
    public MyMap myMap() {
        String[][] level1 = {
                {".", ".", ".", "1"},
                {".", "2", "4", "."},
                {".", ".", "2", "."},
                {".", "3", ".", "."},
                {".", ".", "4", "3"},
                {"1", ".", ".", "."}
        };
        return new MyMap(level1);
    }


    @Bean
    public Logic_NumberLink logicNumberLink(MyMap myMap) {
        return new Logic_NumberLink(myMap);
    }

    @Bean
    @Profile("!test")
    public CommandLineRunner runner(NumberLink numberLink) {
        return args -> numberLink.play();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ScoreService scoreService(){
        return new ScoreServiceRestClient();
    }

    @Bean
    public CommentService commentService(){
        return new CommentServiceRestClient();
    }

    @Bean
    public RatingService ratingService(){
        return new RatingServiceRestClient();
    }

}
