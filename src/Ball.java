
public class Ball extends Point {
    private static int indexBall = 0;
    private boolean state;
    private Cell cell;
    private int index;
    private int preX;
    private int preY;
    private int countStep;
    private int costStep;

    public Ball(int x, int y) {
        super(x, y);
        preX = x;
        preY = y;
        indexBall++;
        state = true;
        this.index = indexBall;
    }

    public void backStep(){
        if (preX != getX() || preY != getY())
            countStep--;
        if (countStep < 0)
            countStep = 0;
        setX(preX);
        setY(preY);
    }

    public boolean nextX(int dir) {
        if (!state)
            return false;
        if (dir == 0)
            return false;
        if (dir < 0 && (cell.getWall() & Cell.LEFT) != 0)
            return false;
        if (dir > 0 && (cell.getWall() & Cell.RIGHT) != 0)
            return false;
        int x = getX() + dir / Math.abs(dir);
        Cell nextCell = Main.getCell(x, getY());
        if (nextCell == null)
            return false;
        if (nextCell.getHole() != null) {
            if (nextCell.getHole().getIndex() != index)
                return false;
            else {
                nextCell.getHole().setState(false);
                state = false;
            }
        }
        preX = getX();
        preY = getY();
        setX(x);
        countStep++;
        setCell(Main.getCell(getX(), getY()));
        return true;
    }

    public boolean nextY(int dir) {
        if (!state)
            return false;
        if (dir == 0)
            return false;
        if (dir < 0 && (cell.getWall() & Cell.TOP) != 0)
            return false;
        if (dir > 0 && (cell.getWall() & Cell.BOTTOM) != 0)
            return false;
        int y = getY() + dir / Math.abs(dir);
        Cell nextCell = Main.getCell(getX(), y);
        if (nextCell == null)
            return false;
        if (nextCell.getHole() != null) {
            if (nextCell.getHole().getIndex() != index)
                return false;
            else {
                nextCell.getHole().setState(false);
                state = false;
            }
        }
        preX = getX();
        preY = getY();

        setY(y);
        countStep++;
        setCell(Main.getCell(getX(), getY()));
        return true;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public int getIndex() {
        return index;
    }

    public void setCell(Cell cell) {
        cell.setSpoor(this.index);
        this.cell = cell;
    }

    public int getNextX(int dir){
        if (dir == 0)
            return getX();
        if (dir < 0 && (cell.getWall() & Cell.LEFT) != 0)
            return getX();
        if (dir > 0 && (cell.getWall() & Cell.RIGHT) != 0)
            return getX();
        return getX() + dir / Math.abs(dir);
    }

    public int getNextY(int dir){
        if (dir == 0)
            return getY();
        if (dir < 0 && (cell.getWall() & Cell.TOP) != 0)
            return getY();
        if (dir > 0 && (cell.getWall() & Cell.BOTTOM) != 0)
            return getY();
        return getY() + dir / Math.abs(dir);
    }

    public int getCountStep() {
        return countStep;
    }

    protected Ball clone() {
        Ball ball = new Ball(getX(), getY());
        ball.setCell(this.cell);
        ball.setState(state);
        ball.countStep = countStep;
        ball.index = index;
        return ball;
    }

    public void clearStep(){
        countStep = 0;
    }
}
