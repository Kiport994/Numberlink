package sk.tuke.gamestudio.service.rating;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sk.tuke.gamestudio.entity.Rating;

import java.util.Arrays;
import java.util.List;

@Service
public class RatingServiceRestClient implements RatingService {
    private final String url = "http://localhost:8080/api/rating";

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void addRating(Rating rating) {
        restTemplate.postForEntity(url, rating, Rating.class);
    }

    @Override
    public List<Rating> getTopRate(String gameName) {
        return Arrays.asList(restTemplate.getForEntity(url + "/" + gameName, Rating[].class).getBody());
    }

    @Override
    public void resetRating() {
        throw new UnsupportedOperationException("Not supported via web service");
    }
}

