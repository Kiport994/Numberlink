package sk.tuke.gamestudio.game.map.tile;

public class PathTile extends Tile {
    private String symbol;

    public PathTile(int row, int col) {
        super(row, col, ".");
        this.symbol = ".";
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }
}
