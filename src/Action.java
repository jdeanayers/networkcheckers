
import java.io.Serializable;

class Action implements Serializable {

    public boolean endOfTurn;
    public int oldX;
    public int oldY;
    public int newX;
    public int newY;

    public Action(boolean eot, int ox, int oy, int nx, int ny) {
        endOfTurn = eot;
        oldX = ox;
        oldY = oy;
        newX = nx;
        newY = ny;
    }
}
