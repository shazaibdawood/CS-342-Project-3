import java.io.Serializable;
import java.util.ArrayList;

public class UserInfo implements Serializable {
    public String username;
    public String password;
    public int wins;
    public int losses;
    public int draws;
    public int totalGames;
    public ArrayList<String> friends = new ArrayList<>();

}
