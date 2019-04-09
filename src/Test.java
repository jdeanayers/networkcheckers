
import java.io.Serializable;

class Action implements Serializable {

    public boolean endOfTurn;
    public String blah;

    public Action(String s) {
        blah = s;
    }
}
