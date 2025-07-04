package sk.tuke.gamestudio.game.map.tile;

public abstract class Tile {
    protected int row;
    protected int col;
    protected final String originalSymbol;

    public Tile(int row, int col, String originalSymbol) {
        this.row = row;
        this.col = col;
        this.originalSymbol = originalSymbol;
    }

    public abstract String getSymbol();
}
