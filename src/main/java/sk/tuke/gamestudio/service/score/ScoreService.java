package sk.tuke.gamestudio.service.score;

import sk.tuke.gamestudio.entity.Score;

import java.util.List;

public interface ScoreService {
    void addScore(Score score) throws ScoreException;
    List<Score> getTopScores() throws ScoreException;
    void resetScores() throws ScoreException;
}
