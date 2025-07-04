package sk.tuke.gamestudio.server.webservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sk.tuke.gamestudio.entity.Rating;
import sk.tuke.gamestudio.service.rating.RatingService;

import java.util.List;

@RestController
@RequestMapping("/api/rating")
public class RatingServiceRest {

    @Autowired
    private RatingService ratingService;

    @GetMapping("/{game}")
    public List<Rating> getTopRatings(@PathVariable String game) {
        return ratingService.getTopRate(game);
    }

    @PostMapping
    public void addRating(@RequestBody Rating rating) {
        ratingService.addRating(rating);
    }
}
