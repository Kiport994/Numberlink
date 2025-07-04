package sk.tuke.gamestudio.entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.NamedQuery;

import java.io.Serializable;
import java.util.Date;

@Entity
@NamedQuery(
        name = "Rating.getTopRate",
        query = "SELECT s FROM Rating s WHERE s.game=:game ORDER BY s.rating DESC"
)
@NamedQuery(
        name = "Rating.resetRating",
        query = "DELETE FROM Rating "
)


public class Rating implements Serializable {
    @Id
    @GeneratedValue
    private int ident;
    private String player;
    private String game;
    private Integer rating;
    private Date ratedOn;

    public Rating() {}

    public Rating(String player, String game, Integer rating, Date ratedOn) {
        this.player = player;
        this.game = game;
        this.rating = rating;
        this.ratedOn = ratedOn;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Date getRatedOn() {
        return ratedOn;
    }

    public void setRatedOn(Date ratedOn) {
        this.ratedOn = ratedOn;
    }

    public int getIdent() {
        return ident;
    }

    public void setIdent(int ident) {
        this.ident = ident;
    }
}
