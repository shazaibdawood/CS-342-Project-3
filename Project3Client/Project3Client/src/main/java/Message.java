import java.io.Serializable;

public class Message implements Serializable {
    static final long serialVersionUID = 42L;
    UserInfo userInfo = new UserInfo();

    String recipient;
    String message;
    int[][] board = new int[6][7];
    boolean isMove = false;
    boolean isLogin = false;

    boolean loginCheck = false;
    boolean isWin  = false;

    public Message(){
        for(int i = 0; i < 6; i++){
            for(int j = 0; j < 7; j++){
                board[i][j] = 0;
            }
        }
    }

    public String toString(){
        return message;
    }
    public void setMessage(String message){
        this.message = message;
    }
    public int[][] getBoard(){
        return board;
    }
    public void setBoard(int[][] board){
        this.board = board;
    }
    public boolean isMove(){
        return isMove;
    }
    public void setMove(boolean isMove){
        this.isMove = isMove;
    }
    public String getRecipient(){
        return recipient;
    }
    public void setRecipient(String recipient){
        this.recipient = recipient;
    }
    public boolean isLogin(){
        return isLogin;
    }
    public void setLogin(boolean isLogin){
        this.isLogin = isLogin;
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
}
