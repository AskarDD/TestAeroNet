/**
 * Created by Сайида on 01.08.2017.
 */
public class Hole extends Point {
    private static int indexHole = 0;
    private boolean state = true;
    private int index;
    public Hole(int x, int y) {
        super(x, y);
        indexHole++;
        this.index = indexHole;
        state = true;
    }

    public boolean isState(){
        return state;
    }

    public void setState(boolean state){
        this.state = state;
    }

    public int getIndex() {
        return index;
    }

    protected Hole clone() {
        Hole hole = new Hole(getX(), getY());
        hole.index = index;
        hole.state = state;
        return hole;
    }
}
