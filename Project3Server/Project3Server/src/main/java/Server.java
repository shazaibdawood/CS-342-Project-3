import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Key;
import java.util.*;
import java.util.function.Consumer;
import java.io.FileWriter;
import java.io.File;


import javafx.application.Platform;
import javafx.scene.control.ListView;
//import jdk.internal.org.objectweb.asm.tree.analysis.Value;


public class Server{

	int count = 1;
	ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
	TheServer server;
	private Consumer<Message> callback;

	HashMap<String, UserInfo> users = new HashMap<>();
	Queue<ClientThread> randomMatchMaking = new LinkedList<>();

	Server(Consumer<Message> call){
		callback = call;
		server = new TheServer();
		server.start();

		try {
			File myFile = new File("userinfo.txt");
			Scanner scanner = new Scanner(myFile);
			while (scanner.hasNextLine()) {
				String rawData = scanner.nextLine();
				String[] formattedData = rawData.split(",");

				UserInfo u = new UserInfo();
				u.username = formattedData[0];
				u.password = formattedData[1];
				u.wins = Integer.parseInt(formattedData[2]);
				u.losses = Integer.parseInt(formattedData[3]);
				u.draws = Integer.parseInt(formattedData[4]);
				u.totalGames = Integer.parseInt(formattedData[5]);

				if(formattedData.length == 7 && !formattedData[6].isEmpty()){
					String[] friends = formattedData[6].split(";");
					for(String friend : friends){
						u.friends.add(friend);
					}
				}
				users.put(u.username, u);
			}
			scanner.close();
		} catch (Exception e) {
			System.out.println("Error reading user info");
			e.printStackTrace();
		}

	}

	public HashMap<String, Boolean> getAllUsers() {
		HashMap<String, Boolean> userStatusMap = new HashMap<>();

		for (String username : users.keySet()) {
			boolean isOnline = false;

			for (ClientThread client : clients) {
				if (client.clientMessage.userInfo != null && username.equals(client.clientMessage.userInfo.username)) {
					isOnline = true;
					break;
				}
			}

			userStatusMap.put(username, isOnline);
		}

		return userStatusMap;
	}

	public void saveUserData() {
		System.out.println("Saving user info");
		try {
			FileWriter myWriter = new FileWriter("userinfo.txt");
			for(UserInfo u : users.values()) {
				System.out.println(u.username + "," + u.password + "," + u.wins + "," + u.losses + "," + u.draws + "," + u.totalGames + "," + String.join(";", u.friends));
				myWriter.write(u.username + "," + u.password + "," + u.wins + "," + u.losses + "," + u.draws + "," + u.totalGames + "," + String.join(";", u.friends) + "\n");
			}
			myWriter.close();
		} catch (IOException e) {
			System.out.println("Error saving user info");
			e.printStackTrace();
		}
	}

	public boolean checkLogin(Message message) {
		if(users.containsKey(message.userInfo.username)){
			boolean check = (users.get(message.userInfo.username).password.equals(message.userInfo.password));
			if(!check){
				message.warning = "invalid password";
			}
			return check;
		}
		message.warning = "user not found";
		return false;
	}
	public boolean checkSignUp(Message message) {
		if(!users.containsKey(message.userInfo.username)){
			if(message.userInfo.username.isEmpty() || message.userInfo.password.isEmpty()){
				message.warning = "field cannot be empty";
				return false;
			} else if (!isAlNum(message.userInfo.username)) {
				message.warning = "username must be alphanumeric";
				return false;
			}
			else {
				users.put(message.userInfo.username, message.userInfo);
				return true;
			}
		}
		message.warning = "username is taken";
		return false;
	}

	public boolean pair2ClientsRandomly() {
		if (randomMatchMaking.size() >= 2) {
//			ClientThread p1 = randomMatchMaking.remove();
//			ClientThread p2 = randomMatchMaking.remove();
//			System.out.println("Before pairing clients:");
//			System.out.println("P1: " + p1.clientMessage.userInfo.username + " P2: " + p2.clientMessage.userInfo.username);
//			p1.clientMessage.setOpponent(p2.clientMessage.userInfo.username);
//			p1.clientMessage.setType("Paired");
//			p2.clientMessage.setOpponent(p1.clientMessage.userInfo.username);
//			p2.clientMessage.setType("Paired");
//			System.out.println("After pairing clients:");
//			System.out.println("P1: " + p1.clientMessage.userInfo.username + " P2: " + p2.clientMessage.userInfo.username);
//
//
//			try {
//				p1.out.writeObject(p1.clientMessage);
//				p2.out.writeObject(p2.clientMessage);
////				for(ClientThread client : clients){
////					if(p1.clientMessage.userInfo.username == client.clientMessage.userInfo.username){
////						client.out.writeObject(p1.clientMessage);
////					}
////					if(p2.clientMessage.userInfo.username == client.clientMessage.userInfo.username){
////						client.out.writeObject(p1.clientMessage);
////					}
////
////				}
//
//				System.out.println("Pairing clients:");
//				System.out.println(p1.clientMessage.toString());
//				System.out.println(p2.clientMessage.toString());
//
//				return true;
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
			ClientThread p1 = randomMatchMaking.remove();
			ClientThread p2 = randomMatchMaking.remove();
//			System.out.println("Before pairing clients:");
//			System.out.println("P1: " + p1.clientMessage.userInfo.username + " P2: " + p2.clientMessage.userInfo.username);

			Message message1 = new Message();
			message1.userInfo = p1.clientMessage.userInfo;
			message1.setOpponent(p2.clientMessage.userInfo.username);
			message1.setType("Paired");
			message1.allUsers = getAllUsers();
			message1.users = users;
			message1.setIndex(p1.count);
			message1.setLoginCheck(true);

			Message message2 = new Message();
			message2.userInfo = p2.clientMessage.userInfo;
			message2.setOpponent(p1.clientMessage.userInfo.username);
			message2.setType("Paired");
			message2.allUsers = getAllUsers();
			message2.users = users;
			message2.setIndex(p2.count);
			message2.setLoginCheck(false);

			try {
				p1.out.writeObject(message1);
				p2.out.writeObject(message2);
				p1.clientMessage = message1;
				p2.clientMessage = message2;
				updateClientMessage(message1);
				updateClientMessage(message2);

//				System.out.println("Pairing clients:");
//				System.out.println(message1.toString());
//				System.out.println(message2.toString());
				randomMatchMaking.clear();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return false;
	}
	private void updateClientMessage(Message message){
		users.put(message.userInfo.username, message.userInfo);
		for(ClientThread client : clients){
			if(client.clientMessage.userInfo.username.equals(message.userInfo.username)){
				client.clientMessage = message;
				return;
			}
		}

	}
	public void serverGameStart(Message message){
		ClientThread p1 = findUser(message.userInfo.username);
		Message message1 = new Message();
		message1.userInfo = p1.clientMessage.userInfo;
		message1.setOpponent("SERVER");
		message1.setType("Paired");
		message1.allUsers = getAllUsers();
		message1.users = users;
		message1.setIndex(p1.count);
		message1.setLoginCheck(true);

		try {
			p1.out.writeObject(message1);
			p1.clientMessage = message1;
			updateClientMessage(message1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendChat(Message message) {
		String chat = message.getMessage();
		ClientThread opponent = findUser(message.getOpponent());
		ClientThread sender = findUser(message.userInfo.username);
		if(opponent != null){
//			System.out.println("To: " + message.getOpponent() +"Type: " +  message.getType()+ "From: " +message.userInfo.username);
			Message message1 = new Message();
			message1.userInfo = opponent.clientMessage.userInfo;
			message1.setOpponent(opponent.clientMessage.getOpponent());
			message1.setType("Receive Chat");
			message1.allUsers = getAllUsers();
			message1.users = users;
			message1.setIndex(opponent.count);
			message1.setMessage(chat);
			message1.setBoard(opponent.clientMessage.getBoard());

			try {
				opponent.out.writeObject(message1);
				opponent.clientMessage = message1;
				updateClientMessage(message1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else {
			System.out.println("opponent is null");
		}
		if(sender != null){
			Message message2 = new Message();
			message2.userInfo = message.userInfo;
			message2.setOpponent(message.getOpponent());
			message2.setType("Send Chat");
			message2.allUsers = getAllUsers();
			message2.users = users;
			message2.setIndex(message.getIndex());
			message2.setMessage(chat);
			try {
				sender.out.writeObject(message2);
				sender.clientMessage = message2;
				updateClientMessage(message2);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else {
			System.out.println("sender is null");
		}
	}
	public void serverMove(Message message) {
		ClientThread p1 = findUser(message.userInfo.username);

		Message message1 = new Message();
		message1.userInfo = p1.clientMessage.userInfo;
		message1.setOpponent("SERVER");
		message1.setType("Receive Move");
		message1.allUsers = getAllUsers();
		message1.users = users;
		message1.setIndex(p1.count);
		message1.setLoginCheck(true);

		String gameStatus = checkWin(message.getBoard());
		if(gameStatus.equals("Win")){
			message1.setType("Win");
			message1.userInfo.wins++;
			message1.userInfo.totalGames++;
			message1.setBoard(message.getBoard());
			callback.accept(message1);
		} else if (gameStatus.equals("Draw")) {
			message1.setType("Draw");
			message1.userInfo.draws++;
			message1.userInfo.totalGames++;
			message1.setBoard(message.getBoard());
			callback.accept(message1);
		} else {
			message1.setType("Receive Move");
			int[][] serverBoard = serverCalculateMove(reverseBoard(message.getBoard()));

			gameStatus = checkWin(serverBoard);
			if(gameStatus.equals("Win")){
				message1.setType("Lose");
				callback.accept(message1);
			} else if (gameStatus.equals("Draw")) {
				message1.setType("Draw");
				callback.accept(message1);
			} else {
				message1.setType("Receive Move");
			}

			message1.setBoard(reverseBoard(serverBoard));
		}
		try {
			p1.out.writeObject(message1);
			p1.clientMessage = message1;
			updateClientMessage(message1);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	public void sendMove(Message message) {
		if(message.getOpponent().equals("SERVER")){
			serverMove(message);
			return;
		}
		ClientThread p1 = findUser(message.userInfo.username);
		ClientThread p2 = findUser(message.getOpponent());

		Message message1 = new Message();
		message1.userInfo = p1.clientMessage.userInfo;
		message1.setOpponent(p2.clientMessage.userInfo.username);
		message1.setType("Send Move");
		message1.allUsers = getAllUsers();
		message1.users = users;
		message1.setIndex(p1.count);
		message1.setLoginCheck(false);

		Message message2 = new Message();
		message2.userInfo = p2.clientMessage.userInfo;
		message2.setOpponent(p1.clientMessage.userInfo.username);
		message2.setType("Receive Move");
		message2.allUsers = getAllUsers();
		message2.users = users;
		message2.setIndex(p2.count);
		message2.setLoginCheck(true);

		String gameStatus = checkWin(message.getBoard());
		if(gameStatus.equals("Win")){
			message1.setType("Win");
			message1.userInfo.wins++;
			message1.userInfo.totalGames++;

			message2.setType("Lose");
			callback.accept(message1);
			message1.userInfo.losses++;
			message1.userInfo.totalGames++;
		} else if (gameStatus.equals("Draw")) {
			message1.setType("Draw");
			message1.userInfo.draws++;
			message1.userInfo.totalGames++;

			message2.setType("Draw");
			message1.userInfo.draws++;
			message1.userInfo.totalGames++;
			callback.accept(message1);
		} else {
			message1.setType("Send Move");
			message2.setType("Receive Move");
		}
		message1.setBoard(message.getBoard());
		message2.setBoard(reverseBoard(message.getBoard()));

		try {
			p1.out.writeObject(message1);
			p2.out.writeObject(message2);
			p1.clientMessage = message1;
			p2.clientMessage = message2;
			updateClientMessage(message1);
			updateClientMessage(message2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void quitOrExitGame(Message message) {
		ClientThread p1 = findUser(message.userInfo.username);
		ClientThread p2 = findUser(message.getOpponent());
		if(message.getType().equals("Exit")) {
			p2 = null;
		}

		Message message1 = new Message();
		message1.userInfo = p1.clientMessage.userInfo;
		message1.setOpponent("");
		message1.setType(message.getType());
		message1.allUsers = getAllUsers();
		message1.users = users;
		message1.setIndex(p1.count);
		message1.setLoginCheck(false);
		message1.setBoard(message.getBoard());

		Message message2 = new Message();
		if(p2 != null) {
			message2.userInfo = p2.clientMessage.userInfo;
			message2.setOpponent(p1.clientMessage.userInfo.username);
			message2.setType(p2.clientMessage.getType());
			message2.allUsers = getAllUsers();
			message2.users = users;
			message2.setIndex(p2.count);
			message2.setLoginCheck(true);
			message2.setBoard(reverseBoard(message.getBoard()));
		}

		if(message.getType().equals("Quit")){
			message1.setType("Quit");
			message1.userInfo.losses++;
			message1.userInfo.totalGames++;
			message1.setOpponent("");

			message2.setType("Win");
			message2.userInfo.wins++;
			message2.userInfo.totalGames++;
			updateClientMessage(message1);
			updateClientMessage(message2);
		} else if(message.getType().equals("Exit")){
			message1.setType("Exit");
			message1.setOpponent("");
		}

		try {
			if(p1 != null){
				p1.out.writeObject(message1);
				p1.clientMessage = message1;
				updateClientMessage(message1);
			}
			if(message.getType().equals("Quit")) {
				if (p2 != null) {
					p2.out.writeObject(message2);
					p2.clientMessage = message2;
					updateClientMessage(message2);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private String checkWin(int[][] board){
		for(int i = 0; i < board.length; i++){
			for (int j = 0; j < board[i].length; j++) {
				if(at(board, i, j) == 1 && at(board, i, j + 1) == 1 && at(board, i, j + 2) == 1 && at(board, i, j + 3) == 1){
					return "Win";
				} else if(at(board, i, j) == 1 && at(board, i + 1, j) == 1 && at(board, i + 2, j) == 1 && at(board, i + 3, j) == 1){
					return "Win";
				} else if (at(board, i, j) == 1 && at(board, i + 1, j + 1) == 1 && at(board, i + 2, j + 2) == 1 && at(board, i + 3, j + 3) == 1) {
					return "Win";
				} else if (at(board, i, j) == 1 && at(board, i + 1, j - 1) == 1 && at(board, i + 2, j - 2) == 1 && at(board, i + 3, j - 3) == 1) {
					return "Win";
				}
			}
		}

		for(int i = 0; i < board.length; i++){
			for (int j = 0; j < board[i].length; j++) {
				if(board[i][j] == 0){
					return "Continue";
				}
			}
		}

		return "Draw";


	}

	private int[][] serverCalculateMove(int[][] board){
		int[][] move = board.clone();
		for(int i = 5; i >= 0; i--) {
			for(int j = 6; j >= 0; j--) {
				if(move[i][j] == 0){
					move[i][j] = 1;
					return move;
				}
			}
		}

		return move;
	}

	private int[][] reverseBoard(int[][] board){
		int[][] reverse = new int[6][7];
		for(int i = 0; i < 6; i++){
			for(int j = 0; j < 7; j++){
				if(board[i][j] == 1){
					reverse[i][j] = 2;
				} else if (board[i][j] == 2) {
					reverse[i][j] = 1;
				}
				else {
					reverse[i][j] = board[i][j];
				}
			}
		}
		return reverse;
	}

	private int at(int[][] board, int row, int col){
		if(row < board.length && col < board[row].length && row >= 0 && col >= 0){
			return board[row][col];
		}
		return -1;
	}

	private boolean isAlNum(String s){
		return s.matches("[a-zA-Z0-9]+");
	}

	private ClientThread findUser(String username){
		for(ClientThread client : clients){
			if(username.equals(client.clientMessage.userInfo.username)){
				return client;
			}
		}
		System.out.println("user not found");
		return null;
	}

	private void printClients(){
		for(ClientThread client : clients){
			System.out.println(client.clientMessage.toString() + " " + client.clientMessage.userInfo.totalGames);
		}
	}

	public class TheServer extends Thread{

		public void run() {
			try(ServerSocket mysocket = new ServerSocket(5555);){
		    System.out.println("Server is waiting for a client!");

		    while(true) {
				ClientThread c = new ClientThread(mysocket.accept(), count);
//				callback.accept("client has connected to server: " + "client #" + count);
				callback.accept(new Message());
				clients.add(c);
				c.start();

				count++;
			    }
			} catch(Exception e) {
				callback.accept(new Message());
				}
			}
		}

		class ClientThread extends Thread{
			Socket connection;
			int count;
			ObjectInputStream in;
			ObjectOutputStream out;
			Message clientMessage = new Message();

			ClientThread(Socket s, int count){
				this.connection = s;
				this.count = count;
			}

			public void updateClients(Message message, ClientThread client) throws IOException {
				boolean paired = false;
				if(message.getType().equals("Login")){
					message.setIndex(count);
					message.setLoginCheck(checkLogin(message));
					if (message.isLoginCheck()) {
						client.clientMessage.userInfo = users.get(message.userInfo.username);
						message.userInfo = users.get(message.userInfo.username);
					}
				} else if (message.getType().equals("Sign Up")) {
					message.setIndex(count);
					message.setLoginCheck(checkSignUp(message));
				} else if (message.getType().equals("Random Game Start")) {
					randomMatchMaking.add(client);
				} else if (message.getType().equals("Send Chat")) {
					sendChat(message);
					System.out.println("Stepping into Send Chat Method");
				} else if (message.getType().equals("Send Move")) {
					sendMove(message);
				}
				else if (message.getType().equals("Server Game Start")) {
					serverGameStart(message);
				} else if (message.getType().equals("Quit") || message.getType().equals("Exit")) {
					quitOrExitGame(message);
				} else if(message.getType().equals("Disconnect")) {
					users.put(message.userInfo.username, message.userInfo);
					return;
				}

				message.allUsers = getAllUsers();
				message.users = users;
				client.clientMessage = message;
//				System.out.println("Client: " + client.clientMessage.userInfo.username + " " + client.clientMessage.getOpponent() + " " + client.clientMessage.getType());
				paired = pair2ClientsRandomly();
				users.put(message.userInfo.username, message.userInfo);
				if(!paired && !message.getType().equals("Send Chat") && !message.getType().equals("Paired") && !message.getType().equals("Send Move") && !message.getType().equals("Server Game Start")  && !message.getType().equals("Quit") && !message.getType().equals("Exit")) {
					try {
						client.out.writeObject(message);
					}
					catch(Exception e) {}
				}



			}

			public void run(){
				try {
					in = new ObjectInputStream(connection.getInputStream());
					out = new ObjectOutputStream(connection.getOutputStream());
					connection.setTcpNoDelay(true);
				}
				catch(Exception e) {
					System.err.println("Streams not open");
				}

				 while(true) {
					    try {
							Message data = (Message) in.readObject();
							System.out.println("Recieved: " + data.toString());
							callback.accept(data);
							updateClients(data, this);
							System.out.println("Sent: " + this.clientMessage.toString());
							if(this.clientMessage.getType().equals("Paired")){
								callback.accept(this.clientMessage);
							}
							System.out.println("Current list of clients");
							printClients();
							for(ClientThread client : clients){
								updateClientMessage(client.clientMessage);
							}
						}
					    catch(Exception e) {
							e.printStackTrace();
//							callback.accept("OOOOPPs...Something wrong with the socket from client: " + count + "....closing down!");
//							updateClients("Client #"+count+" has left the server!");
							clients.remove(this);
							break;
					    }
				 }
			}//end of run


		}//end of client thread
}






