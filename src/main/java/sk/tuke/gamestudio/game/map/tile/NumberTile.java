package sk.tuke.gamestudio.game.map.tile;

public class NumberTile extends Tile {
    private final int value;
    private boolean used;

    public NumberTile(int row, int col, int value) {
        super(row, col, String.valueOf(value));
        this.value = value;
        this.used = false;
    }

    public boolean isUsed() {
        return used;
    }

    public void markUsed() {
        used = true;
    }

    @Override
    public String getSymbol() {
        return String.valueOf(value);
    }
}
