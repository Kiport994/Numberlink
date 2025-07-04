package sk.tuke.gamestudio.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sk.tuke.gamestudio.game.map.MyMap;
import sk.tuke.gamestudio.game.map.tile.NumberTile;
import sk.tuke.gamestudio.game.map.tile.Tile;
import sk.tuke.gamestudio.entity.Comment;
import sk.tuke.gamestudio.entity.Rating;
import sk.tuke.gamestudio.entity.Score;
import sk.tuke.gamestudio.service.comment.CommentService;
import sk.tuke.gamestudio.service.rating.RatingService;
import sk.tuke.gamestudio.service.score.ScoreService;

import java.util.Date;
import java.util.List;
import java.util.Scanner;

@Component
public class NumberLink {

    @Autowired
    private CommentService commentService;

    @Autowired
    private RatingService ratingService;

    @Autowired
    private ScoreService scoreService;

    public void play() {
        Scanner scanner = new Scanner(System.in);

        String player = askPlayerName(scanner);
        System.out.println("- - - - - - - - - - -");

        List<Score> previousScore = scoreService.getTopScores();

        if (!previousScore.isEmpty()) {
            System.out.println("Hello again " + player);
            System.out.println("\nYour previous results were:");
            for (Score s : previousScore) {
                if (s != null) {
                    System.out.printf("Level: %-7s | Time: %3d sec\n", s.getGame(), s.getPoints());
                }

            }
            System.out.println("- - - - - - - - - - -");
        }

        GameSetup setup = askGameSetup(scanner);
        System.out.println("- - - - - - - - - - -");
        MyMap map = setup.map;
        String levelName = setup.levelName;

        List<Rating> previousRate = ratingService.getTopRate(levelName);
        List<Comment> previousComment = commentService.getLastComment(levelName);

        if (!previousRate.isEmpty() && !previousScore.isEmpty()) {
            System.out.println("Your previous ratings about this level were:");
            for (Rating r : previousRate) {
                if (r != null) {
                    System.out.println(r.getRating());
                }
            }
            System.out.println("- - - - - - - - - - -");
            System.out.println("Your previous comments about this level were:");
            for (Comment c : previousComment) {
                if (c != null) {
                    System.out.println(c.getComment());
                }
            }
            System.out.println("- - - - - - - - - - -");
        }

        map.printMap();

        Logic_NumberLink game = new Logic_NumberLink(map);

        long startTime = System.currentTimeMillis();
        int seconds = 0;

        while (true) {
            int checker = 0;
            System.out.println("\nChoose coordinates (row col) or 'exit' if you want leave:");

            String line = scanner.nextLine().trim();
            if (line.equalsIgnoreCase("exit")) {
                seconds = 0;
                System.out.println("- - - - - - - - - - -");
                break;
            }

            String[] coords = line.split("\\s+");
            if (coords.length != 2) {
                System.out.println("Please enter exactly two integers, or 'exit'.");
                continue;
            }

            int startRow;
            int startCol;
            try {
                startRow = Integer.parseInt(coords[0]);
                startCol = Integer.parseInt(coords[1]);
                game.setStart(startRow, startCol);
            } catch (NumberFormatException e) {
                System.out.println("Coordinates must be integers. Try again.");
                continue;
            } catch (IndexOutOfBoundsException | IllegalArgumentException e) {
                System.out.println(e.getMessage());
                continue;
            }

            System.out.println("You choose number: " + game.getDigit());
            String[][] backup = map.backupMap();
            while (true) {
                boolean stepOver = processNextStep(scanner, map, game, startRow, startCol, backup);
                if (stepOver) {
                    break;
                }
            }

            for (int x = 0; x < map.getRows(); x++) {
                for (int y = 0; y < map.getCols(); y++) {
                    if (map.getTileSymbol(x, y).equals(".")) {
                        checker++;
                    }
                }
            }

            if (checker == 0) {
                System.out.println("Congratulation!!!\n");
                long endTime = System.currentTimeMillis();
                long millis = endTime - startTime;
                seconds = (int) (millis / 1000);
                System.out.println("You completed the " + levelName + " level in " + seconds + " seconds!");
                break;
            }
        }

        int rating = askRating(scanner);
        System.out.println("- - - - - - - - - - -");
        String comment = askComment(scanner);
        System.out.println("- - - - - - - - - - -");

        System.out.println("Thats all, thank u for playing\nSee you soon!");
        scanner.close();

        Comment comment_JDBC = new Comment(player, levelName, comment, new Date());
        Rating rating_JDBC = new Rating(player, levelName, rating, new Date());
        Score score_JDBC = new Score(levelName, player, seconds, new Date());

        commentService.addComment(comment_JDBC);
        ratingService.addRating(rating_JDBC);
        scoreService.addScore(score_JDBC);
    }

    private static String askPlayerName(Scanner scanner) {
        System.out.println("Hello and welcome to NUMBERLINKS!");
        System.out.print("Enter your name to start a game: ");
        return scanner.nextLine();
    }

    private static GameSetup askGameSetup(Scanner scanner) {
        MyMap map = null;
        String levelName = "";

        while (true) {
            System.out.println("Choose a level: 1 - Easy, 2 - Medium, 3 - Hard");
            String input = scanner.nextLine().trim();
            try {
                int level = Integer.parseInt(input);
                switch (level) {
                    case 1 -> {
                        map = getMyMap(1);
                        levelName = "easy";
                    }
                    case 2 -> {
                        map = getMyMap(2);
                        levelName = "medium";
                    }
                    case 3 -> {
                        map = getMyMap(3);
                        levelName = "hard";
                    }
                    default -> System.out.println("Invalid level. Please enter a number between 1 and 3");
                }
                if (map != null) break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter 1, 2 or 3.");
            }
        }
        return new GameSetup(map, levelName);
    }

    private static MyMap getMyMap(int level) {
        String[][] level1 = {
                {".", ".", ".", "1"},
                {".", "2", "4", "."},
                {".", ".", "2", "."},
                {".", "3", ".", "."},
                {".", ".", "4", "3"},
                {"1", ".", ".", "."}
        };

        String[][] level2 = {
                {"1", ".", ".", "6", ".", ".", ".", "9"},
                {"1", "2", ".", "4", ".", ".", ".", "."},
                {".", ".", ".", ".", "3", ".", ".", "."},
                {".", "2", ".", ".", "4", ".", ".", "."},
                {".", ".", ".", ".", ".", "6", ".", "."},
                {"3", ".", ".", "5", ".", ".", "7", "."},
                {".", "5", ".", ".", ".", ".", "8", "9"},
                {".", ".", ".", "7", ".", ".", ".", "8"}
        };

        String[][] level3 = {
                {"1", ".", ".", ".", ".", ".", "5", "."},
                {".", ".", ".", ".", ".", ".", "4", "."},
                {"2", ".", ".", ".", ".", ".", ".", "."},
                {".", ".", "6", ".", "2", ".", "4", "5"},
                {"3", ".", ".", ".", "8", ".", ".", "."},
                {".", ".", "3", ".", ".", ".", "8", "."},
                {"6", "7", ".", ".", ".", "7", "1", "."}
        };

        return switch (level) {
            case 1 -> new MyMap(level1);
            case 2 -> new MyMap(level2);
            case 3 -> new MyMap(level3);
            default -> null;
        };
    }

    private boolean processNextStep(Scanner scanner, MyMap map, Logic_NumberLink game,
                                    int startRow, int startCol, String[][] backup) {
        while (true) {
            System.out.println("\nChoose your next coordinates (row col) or 'back' to cancel:");
            String line = scanner.nextLine().trim();

            if (line.equalsIgnoreCase("back")) {
                map.restoreMap(backup);
                map.printMap();
                return true;
            }

            String[] parts = line.split("\\s+");
            if (parts.length != 2) {
                System.out.println("Incorrect input. Please try again.");
                continue;
            }

            int nextRow, nextCol;
            try {
                nextRow = Integer.parseInt(parts[0]);
                nextCol = Integer.parseInt(parts[1]);
            } catch (NumberFormatException ex) {
                System.out.println("Invalid input. Please enter two numbers like: 1 3");
                return false;
            }

            boolean stepSuccess = game.moveToTarget(nextRow, nextCol);
            if (!stepSuccess) {
                map.restoreMap(backup);
                map.printMap();
                return true;
            }
            map.printMap();

            Tile tile = map.getTile(nextRow, nextCol);
            if (tile instanceof NumberTile && !(nextRow == startRow && nextCol == startCol)) {
                System.out.println("Target cell reached: " + game.getDigit());
                Tile startTile = map.getTile(startRow, startCol);
                Tile targetTile = map.getTile(nextRow, nextCol);
                if (startTile instanceof NumberTile) {
                    ((NumberTile) startTile).markUsed();
                }
                if (targetTile instanceof NumberTile) {
                    ((NumberTile) targetTile).markUsed();
                }
                return true;
            }
        }
    }

    private static String askComment(Scanner scanner) {
        while (true) {
            System.out.println("Oh, I almost forgot...\nLeave a comment about this level: ");
            String comment = scanner.nextLine();
            if (!comment.trim().isEmpty()) {
                return comment;
            } else {
                System.out.println("Comment cannot be empty");
            }
        }
    }

    private static int askRating(Scanner scanner) {
        while (true) {
            System.out.println("Thank you for playing!\nRate my game from 1 to 12 please: ");
            if (scanner.hasNextInt()) {
                int rating = scanner.nextInt();
                if (rating >= 1 && rating <= 12) {
                    scanner.nextLine();
                    return rating;
                } else {
                    System.out.println("Number must be between 1 and 12");
                }
            } else {
                System.out.println("That's not a number. Try again");
                scanner.next();
            }
        }
    }

    public static class GameSetup {
        public final MyMap map;
        public final String levelName;

        public GameSetup(MyMap map, String levelName) {
            this.map = map;
            this.levelName = levelName;
        }
    }

}
