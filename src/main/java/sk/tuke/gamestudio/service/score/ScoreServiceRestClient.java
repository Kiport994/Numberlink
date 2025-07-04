package sk.tuke.gamestudio.service.score;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sk.tuke.gamestudio.entity.Score;

import java.util.Arrays;
import java.util.List;

@Service
public class ScoreServiceRestClient implements ScoreService {
    private final String url = "http://localhost:8080/api/score";

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void addScore(Score score) {
        restTemplate.postForEntity(url, score, Score.class);
    }

    @Override
    public List<Score> getTopScores() {
        return Arrays.asList(restTemplate.getForEntity(url + "/", Score[].class).getBody());
    }

    @Override
    public void resetScores() {
        throw new UnsupportedOperationException("Not supported via web service");
    }
}