import java.io.Serializable;
import java.util.ArrayList;

public class UserInfo implements Serializable {
    public String username;
    public String password;
    public ArrayList<String> friends = new ArrayList<>();
    public int wins;
    public int losses;
    public int draws;
    public int totalGames;
}
