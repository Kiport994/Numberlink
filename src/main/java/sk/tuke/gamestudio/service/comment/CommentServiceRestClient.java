package sk.tuke.gamestudio.service.comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sk.tuke.gamestudio.entity.Comment;

import java.util.Arrays;
import java.util.List;

@Service
public class CommentServiceRestClient implements CommentService {
    private final String url = "http://localhost:8080/api/comment";

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void addComment(Comment comment) {
        restTemplate.postForEntity(url, comment, Comment.class);
    }

    @Override
    public List<Comment> getLastComment(String gameName) {
        return Arrays.asList(restTemplate.getForEntity(url + "/" + gameName, Comment[].class).getBody());
    }

    @Override
    public void resetComment() {
        throw new UnsupportedOperationException("Not supported via web service");
    }
}

