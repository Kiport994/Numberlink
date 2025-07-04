package sk.tuke.gamestudio.game;

import sk.tuke.gamestudio.game.map.MyMap;
import sk.tuke.gamestudio.game.map.tile.Tile;
import sk.tuke.gamestudio.game.map.tile.PathTile;
import sk.tuke.gamestudio.game.map.tile.NumberTile;
import sk.tuke.gamestudio.game.direction.Direction;

import java.util.HashSet;
import java.util.Set;

public class Logic_NumberLink {
    private final MyMap map;
    private int currentRow, currentCol;
    private String digit;
    private Direction preDirect;
    private final Set<String> connectedDigits = new HashSet<>();
    public Logic_NumberLink(MyMap map) {
        this.map = map;
    }

    public String getDigit() {
        return digit;
    }

    public void setStart(int row, int col) {
        String cellSymbol = map.getTileSymbol(row, col);
        Tile tile = map.getTile(row, col);
        if (!cellSymbol.matches("[1-9]")) {
            throw new IllegalArgumentException("You must choose a number.");
        }
        if (tile instanceof NumberTile numTile) {
            if (numTile.isUsed()) {
                throw new IllegalArgumentException("This number was already used!");
            }
        }
        currentRow = row;
        currentCol = col;
        digit = cellSymbol;
    }

    public void replaceSymbol(String value, String replaceValue) {
        for (int i = 0; i < map.getRows(); i++) {
            for (int j = 0; j < map.getCols(); j++) {
                Tile tile = map.getTile(i, j);
                if (tile instanceof PathTile && tile.getSymbol().equals(value)) {
                    map.setTileSymbol(i, j, replaceValue);
                }
            }
        }
    }

    public String getSymbolDirect(Direction curr, Direction pre) {
        if ((curr == Direction.DOWN && pre == Direction.RIGHT) || (curr == Direction.LEFT && pre == Direction.UP)) {
            return "┐";
        } else if ((curr == Direction.RIGHT && pre == Direction.DOWN) || (curr == Direction.UP && pre == Direction.LEFT)) {
            return "└";
        } else if ((curr == Direction.RIGHT && pre == Direction.UP) || (curr == Direction.DOWN && pre == Direction.LEFT)) {
            return "┌";
        }
        return "┘";
    }

    private boolean horizontalStep(int row, int currentCol, int targetCol, int targetRow, String digit) {
        if (currentCol < targetCol) {
            for (int j = currentCol + 1; j <= targetCol; j++) {
                String curVal = map.getTileSymbol(row, j);
                if (!curVal.equals(".") && !(row == targetRow && j == targetCol && curVal.equals(digit))) {
                    System.out.println("You crossed a different path!");
                    return false;
                }
                map.setTileSymbol(row, j, digit);
            }
        } else {
            for (int j = currentCol - 1; j >= targetCol; j--) {
                String curVal = map.getTileSymbol(row, j);
                if (!curVal.equals(".") && !(row == targetRow && j == targetCol && curVal.equals(digit))) {
                    System.out.println("You crossed a different path!");
                    return false;
                }
                map.setTileSymbol(row, j, digit);
            }
        }
        return true;
    }

    private boolean verticalStep(int col, int currentRow, int targetRow, int targetCol, String digit) {
        if (currentRow < targetRow) {
            for (int i = currentRow + 1; i <= targetRow; i++) {
                String curVal = map.getTileSymbol(i, col);
                if (!curVal.equals(".") && !(i == targetRow && col == targetCol && curVal.equals(digit))) {
                    System.out.println("You crossed a different path!");
                    return false;
                }
                map.setTileSymbol(i, col, digit);
            }
        } else {
            for (int i = currentRow - 1; i >= targetRow; i--) {
                String curVal = map.getTileSymbol(i, col);
                if (!curVal.equals(".") && !(i == targetRow && col == targetCol && curVal.equals(digit))) {
                    System.out.println("You crossed a different path!");
                    return false;
                }
                map.setTileSymbol(i, col, digit);
            }
        }
        return true;
    }

    public boolean moveToTarget(int targetRow, int targetCol) {
        boolean pathSuccessful = true;
        Direction curDirect = null;

        if (targetRow != currentRow && targetCol != currentCol) {
            System.out.println("You crossed a different path!");
            return false;
        }

        if (targetRow == currentRow && targetCol == currentCol) {
            System.out.println("You crossed a different path!");
            return false;
        }

        if (targetRow == currentRow) {
            curDirect = (targetCol > currentCol) ? Direction.RIGHT : Direction.LEFT;
            boolean horizontalSuccess = horizontalStep(currentRow, currentCol, targetCol, targetRow, digit);
            if (horizontalSuccess) {
                replaceSymbol(digit, "-");
            } else {
                pathSuccessful = false;
            }
        }

        if (targetCol == currentCol) {
            curDirect = (targetRow > currentRow) ? Direction.DOWN : Direction.UP;
            boolean verticalSuccess = verticalStep(currentCol, currentRow, targetRow, targetCol, digit);
            if (verticalSuccess) {
                replaceSymbol(digit, "|");
            } else {
                pathSuccessful = false;
            }
        }

        if (!pathSuccessful) {
            return false;
        }

        if (preDirect != null && preDirect != curDirect) {
            map.setTileSymbol(currentRow, currentCol, getSymbolDirect(curDirect, preDirect));
        }

        preDirect = curDirect;

        currentRow = targetRow;
        currentCol = targetCol;

        Tile tile = map.getTile(targetRow, targetCol);
        if (tile instanceof NumberTile && digit.equals(tile.getSymbol())) {
            connectedDigits.add(digit);
        }

        return true;
    }

    public boolean isSolved() {
        for (String digit : map.getAllDigits()) {
            if (!isConnected(digit)) {
                return false;
            }
        }
        return true;
    }

    public boolean isConnected(String digit) {
        return connectedDigits.contains(digit);
    }

    public void clearMaps() {
        if (map != null) {
            map.clearMap();
        }
    }
}
