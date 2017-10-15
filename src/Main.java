
import java.io.*;

public class Main {
    private static File file = new File("startconfig_6.txt");
    private static Cell[][] cells;
    private static Ball[] balls;
    private static Hole[] holes;
    private static int countStep = 0;

    public static void main(String[] args) throws IOException {
        if (file.exists()){
            if (init()){
                int countFinish = 0;
                int length = balls.length;
                while (countFinish != length) {
                    boolean stop = moveBall();
                    countFinish = 0;
                    for (int i = 0; i < length; i++)
                        if (!balls[i].isState())
                            countFinish++;
                }
                System.out.println("count step = " + countStep);
            }
        }
    }
    public static Cell getCell(int i, int j){
        if (i > 0 && i <= cells.length && j > 0 && j <= cells.length)
            return cells[i-1][j-1];
        return null;
    }

    private static boolean validMap(int N, int K, int M, String[] strings){
        if (N < 2){
            System.out.println("Размер карты слишком мал");
            return false;
        }
        if (N > 1000){
            System.out.println("Ограничение на размеры карты составляет 1е+6 ячеек. Сторона не должна превышать 1000 единиц.");
            return false;
        }
        if (N*N < 2*K){
            System.out.println("Некорректное задание количества объектов на карте");
            return false;
        }
        if (strings.length < 3 + 2 * K){
            System.out.println("Введены не все координаты шаров, дыр и стен");
            return false;
        }
        if (strings.length < 3 + 4 * K){
            System.out.println("Введены не все координаты дыр и стен");
            return false;
        }
        if (strings.length < 3 + 4 * K + 4 * M){
            System.out.println("Введены не все координаты стен");
            return false;
        }
        int max = 0;
        for (int i = 3; i < strings.length; i++){
            if (max < Integer.parseInt(strings[i].trim()))
                max = Integer.parseInt(strings[i].trim());
        }
        if (max > N){
            System.out.println("Координата объекта выходит за границы карты");
            return false;
        }
        return true;
    }

    private static boolean init() throws IOException {
        boolean valid = true;
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        String conf = reader.readLine();
        String[] mem = conf.split(" ");
        int N = Integer.parseInt(mem[0].trim());
        int K = Integer.parseInt(mem[1].trim());
        int M = Integer.parseInt(mem[2].trim());

        if (validMap(N, K, M, mem)){
            cells = new Cell[N][N];
            for (int i = 0; i < N; i++)
                for (int j = 0; j < N; j++) {
                    int x = i + 1, y = j + 1;
                    cells[i][j] = new Cell(x, y, K);
                    if (i == 0)
                        cells[i][j].setWall(x - 1, y);
                    else if (i == N-1)
                        cells[i][j].setWall(x + 1, y);
                    if (j == 0)
                        cells[i][j].setWall(x, y - 1);
                    else if (j == N-1)
                        cells[i][j].setWall(x, y + 1);
                }
            int k = 3;

            int n = 0;
            balls = new Ball[K];
            while (k < 3 + 2*K){
                balls[n] = new Ball(Integer.parseInt(mem[k].trim()), Integer.parseInt(mem[k+1].trim()));
                n++;
                k+=2;
            }

            n = 0;
            holes = new Hole[K];
            while (k < 3 + 4*K){
                int xHole = Integer.parseInt(mem[k].trim());
                int yHole = Integer.parseInt(mem[k+1].trim());
                holes[n] = new Hole(xHole, yHole);
                cells[xHole - 1][yHole - 1].addHole(holes[n]);
                cells[xHole - 1][yHole - 1].setHoleDistance(holes[n].getIndex() - 1, 0);
                n++;
                k+=2;
            }

            while (k < mem.length){
                int x1Cell = Integer.parseInt(mem[k].trim());
                int y1Cell = Integer.parseInt(mem[k+1].trim());
                int x2Cell = Integer.parseInt(mem[k+2].trim());
                int y2Cell = Integer.parseInt(mem[k+3].trim());
                if (Math.sqrt(Math.pow(x1Cell - x2Cell, 2) + Math.pow(y1Cell - y2Cell, 2)) > 1){
                    valid = false;
                    System.out.println("Стены должны быть описаны смежными ячейками (ячейки с общей границей)");
                    return false;
                }
                cells[x1Cell - 1][y1Cell - 1].setWall(x2Cell, y2Cell);
                cells[x2Cell - 1][y2Cell - 1].setWall(x1Cell, y1Cell);
                k+=4;
            }
            markCells(holes);
            //for (Hole hole : holes){
            //    System.out.println("indexHole = " + hole.getIndex());
            //    for (int j = 0; j < cells.length; j++){
            //        for (int i = 0; i < cells.length; i++){
            //            int dist = cells[i][j].getHoleDistance(hole.getIndex() - 1);
            //            System.out.print(dist);
            //        }
            //        System.out.println();
            //    }
            //    System.out.println();
            //}

            for (Ball ball : balls){
                ball.setCell(cells[ball.getX() - 1][ball.getY() - 1]);
            }
        }
        return valid;
    }

    public static void markCells(Hole[] holes){
        for (Hole hole : holes){
            for (int t = 1; t < cells.length; t++) {
                for (int i = -t; i <= t; i++) {
                    for (int j = -t; j <= t; j++) {
                        if (Math.abs(i) > t - 1 || Math.abs(j) > t - 1) {
                            int xt = hole.getX() + i;
                            int yt = hole.getY() + j;
                            int minDistance = markNeighbor(xt, yt, hole.getIndex(), false);
                            if (minDistance != -2) {
                                cells[xt - 1][yt - 1].setHoleDistance(hole.getIndex() - 1, minDistance + 1);
                                markNeighbor(xt, yt, hole.getIndex(), true);
                            }
                        }
                    }
                }
            }

        }
    }

    public static int markNeighbor(int xt, int yt, int indexHole, boolean mark){
        boolean culcExe = false;
        int minDistance = 2*cells.length + 1;
        if (xt > 0 && xt <= cells.length && yt > 0 && yt <= cells.length) {
            for (int r = -1; r <= 1; r++) {
                for (int q = -1; q <= 1; q++) {
                    if ((r == 0 || q == 0) && r != q) {
                        int xo = xt + r;
                        int yo = yt + q;
                        if (!cells[xt - 1][yt - 1].isWall(xo, yo)) {
                            if (xo > 0 && xo <= cells.length && yo > 0 && yo <= cells.length) {
                                int distance = cells[xo - 1][yo - 1].getHoleDistance(indexHole - 1);
                                if (!mark) {
                                    if (distance > -1 && minDistance > distance + 1) {
                                        minDistance = distance;
                                        culcExe = true;
                                    }
                                }else {
                                    if (distance == -1) {
                                        cells[xo - 1][yo - 1].setHoleDistance(indexHole - 1, cells[xt - 1][yt - 1].getHoleDistance(indexHole - 1)+1);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (!culcExe)
            return -2;
        return minDistance;
    }

    private static Ball[] goTrack(int x1, int y1, int x2, int y2, int indexBall, int startCycle){
        int dir = 0;
        int x = x1, y = y1;
        boolean lockX = false;
        boolean lockY = false;
        boolean lockFull = lockX && lockY;
        int cycle = startCycle;
        Ball[] vBalls = new Ball[balls.length];
        for (int i = 0; i < balls.length; i++){
            vBalls[i] = balls[i].clone();
        }
        int maxRandStep = Math.max(cells.length / 3, 3);
        int countRandStep = 0;
        boolean rand = false;
        while ((x != x2 || y != y2)){
            switch (cycle){
                case 1 : {
                    while (!lockX) {
                        dir = x2 - x;
                        if (rand) {
                            if (dir == 0)
                                dir = 1 - 2 * (countRandStep % 2);
                            dir = -dir;
                        }
                        lockX = !vBalls[indexBall-1].nextX(dir);
                        if (!lockX){
                            lockY = false;
                            for (int i = 0; i < vBalls.length; i++) {
                                if (i != indexBall - 1) {
                                    int nextX = vBalls[i].getNextX(dir);
                                    Hole hole = cells[nextX - 1][vBalls[i].getY() - 1].getHole();
                                    if (hole != null && hole.isState()) {
                                        if (hole.getIndex() != vBalls[i].getIndex()) {
                                            lockX = true;
                                            vBalls[indexBall-1].backStep();
                                            break;
                                        }else{
                                            vBalls[i].nextX(dir);
                                        }
                                    } else {
                                        vBalls[i].nextX(dir);
                                    }
                                }
                            }
                        }
                        x = vBalls[indexBall-1].getX();
                        if ((!lockY && startCycle == 2) || rand)
                            break;
                    }
                    cycle = 2;
                }break;
                case 2 : {
                    while (!lockY){
                        dir = y2 - y;
                        if (rand) {
                            if (dir == 0)
                                dir = 1 - 2 * (countRandStep % 2);
                            dir = -dir;
                        }
                        lockY = !vBalls[indexBall-1].nextY(dir);
                        if (!lockY){
                            lockX = false;
                            for (int i = 0; i < vBalls.length; i++) {
                                if (i != indexBall - 1) {
                                    int nextY = vBalls[i].getNextY(dir);
                                    Hole hole = cells[vBalls[i].getX() - 1][nextY - 1].getHole();
                                    if (hole != null && hole.isState()) {
                                        if (hole.getIndex() != vBalls[i].getIndex()) {
                                            lockY = true;
                                            vBalls[indexBall-1].backStep();
                                            break;
                                        }
                                    } else {
                                        vBalls[i].nextY(dir);
                                    }
                                }
                            }
                        }
                        y = vBalls[indexBall-1].getY();
                        if ((!lockX && startCycle == 1) || rand)
                            break;
                    }
                    cycle = 1;
                }break;
            }
            lockFull = lockX && lockY;
            rand = false;
            if (lockFull){
                lockX = lockY = false;
                countRandStep++;
                rand = true;
                if (y == y2)
                    cycle = 2;
                if (x == x2)
                    cycle = 1;
            }
            if (countRandStep > maxRandStep)
                return null;
        }
        return vBalls;
    }

    private static Ball[] findTrack(int x1, int y1, int x2, int y2, int indexBall){
        int countMethod = 2;
        Ball[][] vBalls = new Ball[countMethod][balls.length];
        vBalls[0] = goTrack(x1, y1, x2, y2, indexBall,1);
        for (int i = 0; i < holes.length; i++){
            holes[i].setState(true);
        }
        vBalls[1] = goTrack(x1, y1, x2, y2, indexBall,2);
        for (int i = 0; i < holes.length; i++){
            holes[i].setState(true);
        }

        double commonR = balls.length * cells.length;
        int index = 0;
        for (int k = 0; k < countMethod; k++){
            double r = -1;
            if (vBalls[k] != null) {
                r = 0;
                for (int i = 0; i < balls.length; i++) {
                    if (vBalls[k][i].isState())
                        r += Math.sqrt(Math.pow(vBalls[k][i].getX() - holes[i].getX(), 2) + Math.pow(vBalls[k][i].getY() - holes[i].getY(), 2));
                }
                if (r == 0)
                    return vBalls[k];

                if (r > 0 && commonR > r) {
                    commonR = r;
                    index = k;
                }
            }
            else vBalls[k] = null;
        }
        if (commonR == balls.length * cells.length)
            return null;
        return vBalls[index];
    }

    private static boolean moveBall(){
        int length = balls.length;
        Ball[][] vBall = new Ball[length][length];
        for (int k = 0; k < length; k++){
            if (balls[k].isState()) {
                int index = balls[k].getIndex();
                vBall[k] = findTrack(balls[k].getX(), balls[k].getY(), holes[k].getX(), holes[k].getY(), index);
            } else
                vBall[k] = null;
        }
        double commonR = length * cells.length;
        int index = 0;
        for (int k = 0; k < length; k++){
            double r = -1;
            if (vBall[k] != null) {
                r = 0;
                for (int i = 0; i < length; i++) {
                    if (vBall[k][i].isState())
                        r += Math.sqrt(Math.pow(vBall[k][i].getX() - holes[i].getX(), 2) + Math.pow(vBall[k][i].getY() - holes[i].getY(), 2));
                }

                int countStep = vBall[k][k].getCountStep();
                if (commonR > r + countStep) {
                    commonR = r + countStep;
                    index = k;
                }
            }
        }
        for (int i = 0; i < length; i++){
            if (vBall[index] != null) {
                if (vBall[index][i] != null) {
                    balls[i] = vBall[index][i].clone();
                    if (!balls[i].isState()) {
                        Main.getCell(balls[i].getX(), balls[i].getY()).getHole().setState(balls[i].isState());
                    }
                    if (balls[i].getIndex() == index + 1)
                        countStep += balls[i].getCountStep();
                }
            }
            balls[i].clearStep();
        }
        return true;
    }




}
