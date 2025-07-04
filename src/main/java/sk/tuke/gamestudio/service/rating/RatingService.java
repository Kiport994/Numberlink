package sk.tuke.gamestudio.service.rating;

import sk.tuke.gamestudio.entity.Rating;

import java.util.List;

public interface RatingService {
    void addRating(Rating rating) throws RatingException;
    List<Rating> getTopRate(String game) throws RatingException;
    void resetRating() throws RatingException;
}
