
public class Cell extends Point {
    public static byte TOP = 1;
    public static byte BOTTOM = 2;
    public static byte LEFT = 4;
    public static byte RIGHT = 8;
    private byte wall = 0;   // 0 - нет ограничения, 1 - огр-е сверху, 2 - снизу, 4 - слева, 8 - справа
    private Hole hole;
    private int spoorBall = 0; // индекс шарика, который был здесь последним
    private int[] holeDistance;

    public Cell(int x, int y, int K) {
        super(x, y);
        holeDistance = new int[K];
        for (int i = 0; i < K; i++){
            holeDistance[i] = -1;
        }
    }

    public byte getWall() {
        return wall;
    }

    public void setHoleDistance(int indexHole, int distance){
        holeDistance[indexHole] = distance;
    }

    public int getHoleDistance(int indexHole){
        return holeDistance[indexHole];
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

    public boolean isWall(int x, int y){
        boolean result = false;
        if (getY() > y)
            result = (wall & TOP) != 0;
        else if (getY() < y)
            result = (wall & BOTTOM) != 0;
        else if (getX() > x)
            result = (wall & LEFT) != 0;
        else if (getX() < x)
            result = (wall & RIGHT) != 0;
        return result;
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
