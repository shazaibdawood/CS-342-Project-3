import java.io.Serializable;
import java.util.HashMap;

public class Message implements Serializable {
    static final long serialVersionUID = 42L;
    UserInfo userInfo = new UserInfo();

    int index;
    String opponent;
    String message;
    int[][] board = new int[6][7];

    boolean loginCheck = true;
    boolean isWin  = false;
    String type = "";

    String warning;
    HashMap<String, Boolean> allUsers = new HashMap<>();
    HashMap<String, UserInfo> users = new HashMap<>();

    public Message(){
        for(int i = 0; i < 6; i++){
            for(int j = 0; j < 7; j++){
                board[i][j] = 0;
            }
        }
    }

    public String toString(){
        return "Client #" + getIndex() + " Name: " + userInfo.username + " Message: " + getMessage() + " Type: " + getType() + " Opponent: " + getOpponent();
    }
    public String getMessage(){
        return message;
    }
    public void setMessage(String message){
        this.message = message;
    }
    public int[][] getBoard(){
        int[][] copy = new int[6][7];
        for(int i = 0; i < 6; i++){
            for(int j = 0; j < 7; j++){
                copy[i][j] = board[i][j];
            }
        }
        return copy;
    }
    public void setBoard(int[][] board){
        this.board = board;
    }

    public String getOpponent(){
        return opponent;
    }
    public void setOpponent(String recipient){
        this.opponent = recipient;
    }

    public boolean isWin(){
        return isWin;
    }
    public void setWin(boolean isWin){
        this.isWin = isWin;
    }
    public boolean isLoginCheck(){
        return loginCheck;
    }
    public void setLoginCheck(boolean isLoginCheck){
        this.loginCheck = isLoginCheck;
    }

    public String getType(){
        return type;
    }
    public void setType(String type){
        this.type = type;
    }
    public int getIndex(){
        return index;
    }
    public void setIndex(int index){
        this.index = index;
    }
}
