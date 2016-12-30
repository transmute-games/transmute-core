package Meteor.Units;

/**
 * {@code Tuple2i} is a Meteor specific data unit.
 * <br>
 * A data unit that holds 2 integers.
 */
public class Tuple2i {
    public int x, y;

    public Tuple2i(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public Tuple2i() {
    	setPositions(0, 0);
    }
    
    public void setPositions(int x, int y) {
    	this.x = x;
    	this.y = y;
    }

    @Override public String toString() {
        return String.format("(%d, %d)", x, y);
    }
}
