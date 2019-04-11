
import java.io.Serializable;

class Action implements Serializable {

    public boolean endOfTurn;
    public boolean surrender;
    public int oldX;
    public int oldY;
    public int newX;
    public int newY;

    public Action(boolean eot, boolean surrender, int ox, int oy, int nx, int ny) {
        endOfTurn = eot;
        this.surrender = surrender;
        oldX = ox;
        oldY = oy;
        newX = nx;
        newY = ny;
    }
}
