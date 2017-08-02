
public class Cell extends Point {
    public static byte TOP = 1;
    public static byte BOTTOM = 2;
    public static byte LEFT = 4;
    public static byte RIGHT = 8;
    private byte wall = 0;   // 0 - нет ограничения, 1 - огр-е сверху, 2 - снизу, 4 - слева, 8 - справа
    private Hole hole;
    private int spoorBall = 0; // индекс шарика, который был здесь последним

    public Cell(int x, int y) {
        super(x, y);
    }

    public byte getWall() {
        return wall;
    }

    public void setWall(int x, int y) {
        if (getY() > y)
            wall += TOP;
        else if (getY() < y)
            wall += BOTTOM;
        else if (getX() > x)
            wall += LEFT;
        else if (getX() < x)
            wall += RIGHT;
    }

    public void addHole(Hole hole){
        if (hole == null){
            this.hole = null;
            return;
        }
        if (Math.sqrt(Math.pow(getX() - hole.getX(), 2) + Math.pow(getY() - hole.getY(), 2)) == 0)
            this.hole = hole;
    }

    public Hole getHole(){
        return hole;
    }

    public void setSpoor(int index){
        spoorBall = index;
    }
}
