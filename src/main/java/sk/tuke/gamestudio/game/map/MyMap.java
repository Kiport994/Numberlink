package sk.tuke.gamestudio.game.map;

import sk.tuke.gamestudio.game.map.tile.NumberTile;
import sk.tuke.gamestudio.game.map.tile.PathTile;
import sk.tuke.gamestudio.game.map.tile.Tile;

public class MyMap {
    private final int rows;
    private final int cols;
    private final Tile[][] tiles;

    public MyMap(String[][] initialMap) {
        this.rows = initialMap.length;
        this.cols = initialMap[0].length;
        tiles = new Tile[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (initialMap[i][j].equals(".")) {
                    tiles[i][j] = new PathTile(i, j);
                } else {
                    int value = Integer.parseInt(initialMap[i][j]);
                    tiles[i][j] = new NumberTile(i, j, value);
                }
            }
        }
    }

    public Tile getTile(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            throw new IndexOutOfBoundsException("Choose row from 0 to " + (rows - 1) + " and column from 0 to " + (cols - 1));
        }
        return tiles[row][col];
    }

    public void setTileSymbol(int row, int col, String symbol) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            throw new IndexOutOfBoundsException("Choose row from 0 to " + (rows - 1) + " and column from 0 to " + (cols - 1));
        }

        if (tiles[row][col] instanceof PathTile) {
            ((PathTile) tiles[row][col]).setSymbol(symbol);
        }
    }

    public String getTileSymbol(int row, int col) {
        return getTile(row, col).getSymbol();
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public String[][] backupMap() {
        String[][] backup = new String[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                backup[i][j] = getTileSymbol(i, j);
            }
        }
        return backup;
    }


    public void restoreMap(String[][] backup) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {

                if (tiles[i][j] instanceof PathTile) {
                    setTileSymbol(i, j, backup[i][j]);
                }
            }
        }
    }

    public void printMap() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.print(tiles[i][j].getSymbol() + " ");
            }
            System.out.println();
        }
    }

    public void clearMap() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (tiles[i][j] instanceof PathTile) {
                    ((PathTile) tiles[i][j]).setSymbol(".");
                }
            }
        }
    }

    public Set<String> getAllDigits() {
        Set<String> digits = new HashSet<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Tile tile = tiles[i][j];
                if (tile instanceof NumberTile) {
                    digits.add(tile.getSymbol());
                }
            }
        }
        return digits;
    }
}
