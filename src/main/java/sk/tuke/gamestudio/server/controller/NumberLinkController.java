package sk.tuke.gamestudio.server.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;
import sk.tuke.gamestudio.entity.Comment;
import sk.tuke.gamestudio.entity.Rating;
import sk.tuke.gamestudio.entity.Score;
import sk.tuke.gamestudio.game.Logic_NumberLink;
import sk.tuke.gamestudio.game.map.MyMap;
import sk.tuke.gamestudio.service.comment.CommentService;
import sk.tuke.gamestudio.service.rating.RatingService;
import sk.tuke.gamestudio.service.score.ScoreService;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@Scope(WebApplicationContext.SCOPE_SESSION)
public class NumberLinkController implements Serializable {

    @Autowired private ScoreService scoreService;
    @Autowired private CommentService commentService;
    @Autowired private RatingService ratingService;

    private String   playerName;
    private String   level;
    private boolean  gameStarted = false;

    private MyMap            map;
    private Logic_NumberLink game;
    private Instant          startTime;

    private boolean selectingStart = true;
    private int     startRow, startCol;
    private String[][] backup;
    private String  message;

    private boolean gameCompleted = false;

    @RequestMapping(value = "/numberlink", method = RequestMethod.GET)
    public String numberlink(
            @RequestParam(value = "row",    required = false) Integer row,
            @RequestParam(value = "col",    required = false) Integer col,
            @RequestParam(value = "action", required = false) String  action,
            Model model) {

        if (gameStarted && row != null && col != null) {
            handleClick(row, col);
        }

        if ("back".equals(action) && !selectingStart && backup != null) {
            map.restoreMap(backup);
            selectingStart = true;
            message = "Move canceled. Choose a new start cell.";
        }

        List<Score> rawScores = scoreService.getTopScores();
        List<Score> filtered = rawScores.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        model.addAttribute("topScores", filtered);

        String htmlField = buildHtmlField();

        model.addAttribute("playerName",  playerName);
        model.addAttribute("level",       level);
        model.addAttribute("gameStarted", gameStarted);
        model.addAttribute("htmlField",   htmlField);
        model.addAttribute("message",     message);
        model.addAttribute("selectingStart", selectingStart);
        model.addAttribute("gameCompleted", gameCompleted);

        return "numberlink";
    }

    @GetMapping("/numberlink/restart")
    public String restartGame(Model model) {
        this.map = createMap(level);
        this.game = new Logic_NumberLink(map);
        this.selectingStart = true;
        this.message = "Game restarted. Choose your level and start again.";
        this.gameStarted = true;
        this.gameCompleted = false;
        return "redirect:/numberlink";
    }

    @PostMapping("/numberlink/start")
    public String startGame(@RequestParam String playerName,
                            @RequestParam String level,
                            HttpSession session) {

        this.playerName  = playerName;
        session.setAttribute("playerName", playerName);
        this.level = level;
        session.setAttribute("level", level);
        this.gameStarted = true;
        this.selectingStart = true;
        this.startTime   = Instant.now();
        this.message     = "Game started! Choose a number to begin.";

        this.map  = createMap(level);
        this.game = new Logic_NumberLink(map);
        this.gameCompleted = false;

        return "redirect:/numberlink";
    }

    private void handleClick(int row, int col) {
        try {
            if (selectingStart) {
                game.setStart(row, col);
                startRow = row;
                startCol = col;
                selectingStart = false;
                backup   = map.backupMap();
                message  = "Selected number " + game.getDigit() + ". Now choose the target cell.";
                return;
            }

            boolean ok = game.moveToTarget(row, col);
            if (!ok) {
                map.restoreMap(backup);
                message = "Invalid move! Path crossed another path.";
                selectingStart = true;
                return;
            }

            if (map.getTile(row, col) instanceof sk.tuke.gamestudio.game.map.tile.NumberTile &&
                    !(row == startRow && col == startCol)) {
                selectingStart = true;
                message = "Target reached. Choose next number.";
            }

            if (game.isSolved()) {
                int seconds = (int) Duration.between(startTime, Instant.now()).getSeconds();
                message = "Congratulations! Completed level in " + seconds + " s";
                scoreService.addScore(new Score(level, playerName, seconds, new Date()));
                gameStarted = false;
                gameCompleted = true;
            }

        } catch (Exception e) {
            message = e.getMessage();
        }
    }

    @PostMapping("/numberlink/feedback")
    public String submitFeedback(@RequestParam("rating") int rating,
                                 @RequestParam("comment") String comment,
                                 HttpSession session,
                                 Model model) {
        String playerName = (String) session.getAttribute("playerName");
        String level = (String) session.getAttribute("level"); // Или другой параметр игры

        if (playerName == null || level == null) {
            model.addAttribute("error", "Player name or game level missing.");
            return "numberlink";
        }

        Rating newRating = new Rating();
        newRating.setPlayer(playerName);
        newRating.setGame(level);
        newRating.setRating(rating);
        if (newRating.getRatedOn() == null) {
            newRating.setRatedOn(new Date());
        }
        ratingService.addRating(newRating);

        Comment newComment = new Comment();
        newComment.setPlayer(playerName);
        newComment.setGame(level);
        newComment.setComment(comment);
        commentService.addComment(newComment);

        model.addAttribute("success", "Спасибо! Ваш отзыв сохранён.");
        model.addAttribute("rating", rating);
        model.addAttribute("comment", comment);
        this.gameCompleted = false; // Форма должна скрыться

        return "redirect:/numberlink";
    }

    private MyMap createMap(String level) {
        return switch (level) {
            case "easy" -> new MyMap(new String[][]{
                    {".",".",".","1"},
                    {".","2","4","."},
                    {".",".","2","."},
                    {".","3",".","."},
                    {".",".","4","3"},
                    {"1",".",".","."}
            });
            case "medium" -> new MyMap(new String[][]{
                    {"1",".",".","6",".",".",".","9"},
                    {"1","2",".","4",".",".",".","."},
                    {".",".",".",".","3",".",".","."},
                    {".","2",".",".","4",".",".","."},
                    {".",".",".",".",".","6",".","."},
                    {"3",".",".","5",".",".","7","."},
                    {".","5",".",".",".",".","8","9"},
                    {".",".",".","7",".",".",".","8"}
            });
            default -> new MyMap(new String[][]{
                    {"1",".",".",".",".",".","5","."},
                    {".",".",".",".",".",".","4","."},
                    {"2",".",".",".",".",".",".","."},
                    {".",".","6",".","2",".","4","5"},
                    {"3",".",".",".","8",".",".","."},
                    {".",".","3",".",".",".","8","."},
                    {"6","7",".",".",".","7","1","."}
            });
        };
    }

    private String buildHtmlField() {
        if (map == null) return "";
        StringBuilder sb = new StringBuilder("<table>");
        for (int r = 0; r < map.getRows(); r++) {
            sb.append("<tr>");
            for (int c = 0; c < map.getCols(); c++) {
                String sym = map.getTileSymbol(r, c);

                boolean isNumber = sym.matches("[1-9]");
                boolean isConnected = isNumber && game != null && game.isConnected(sym);

                sb.append("<td");

                if (!isConnected)
                    sb.append(" onclick=\"location.href='/numberlink?row=")
                            .append(r).append("&col=").append(c).append("'\"");

                sb.append(" class='").append(isNumber ? "number" : "path").append("'");

                if (isNumber)
                    sb.append(" data-value='").append(sym).append("'");

                if (isConnected)
                    sb.append(" style='background-color: lightgray;'");

                sb.append(">").append(sym).append("</td>");
            }
            sb.append("</tr>");
        }
        sb.append("</table>");
        return sb.toString();
    }

    @GetMapping("/numberlink/exit")
    public String exitToMenu(HttpSession session) {

        this.playerName     = null;
        this.level          = null;
        this.map            = null;
        this.game           = null;
        this.gameStarted    = false;
        this.selectingStart = true;
        this.backup         = null;
        this.message        = null;
        this.gameCompleted  = false;
        this.startTime      = null;

        session.invalidate();

        return "redirect:/numberlink";
    }
}
