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
	private Consumer<Serializable> callback;

	HashMap<String, UserInfo> users = new HashMap<>();
	Queue<ClientThread> randomMatchMaking = new LinkedList<>();

	Server(Consumer<Serializable> call){
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
				if (client.userInfo != null && username.equals(client.userInfo.username)) {
					isOnline = true;
					break;
				}
			}

			userStatusMap.put(username, isOnline);
		}

		return userStatusMap;
	}

	public void saveUserData() {
		try {
			FileWriter myWriter = new FileWriter("userinfo.txt");
			for(UserInfo u : users.values()) {
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

	public boolean pair2ClientsRandomly() throws IOException {
		if(randomMatchMaking.size() >= 2){
			ClientThread p1 = randomMatchMaking.remove();
			ClientThread p2 = randomMatchMaking.remove();
			p1.clientMessage.setOpponent(p2.userInfo.username);
			p1.clientMessage.setType("Paired");
			p2.clientMessage.setOpponent(p1.userInfo.username);
			p2.clientMessage.setType("Paired");

			for(ClientThread client : clients){
				if(p1.clientMessage.userInfo.username == client.clientMessage.userInfo.username){
					client.out.writeObject(p1.clientMessage);
				}
				if(p2.clientMessage.userInfo.username == client.clientMessage.userInfo.username){
					client.out.writeObject(p1.clientMessage);
				}

            }



//			try {
//				p1.out.writeObject(p1.clientMessage);
//				p2.out.writeObject(p2.clientMessage);
//				System.out.println("Pairing clients:");
//				System.out.println(" -> " + p1.clientMessage.userInfo.username + " is paired with " + p2.clientMessage.userInfo.username + p1.clientMessage.getType());
//				System.out.println(" -> " + p2.clientMessage.userInfo.username + " is paired with " + p1.clientMessage.userInfo.username+ p2.clientMessage.getType());
//
//			} catch (Exception e) {
//				e.printStackTrace();
//			}

			return true;
        }

		return false;
	}

	public void sendChat(Message message) {
		String chat = message.toString();
		ClientThread opponent = findUser(message.getOpponent());
		if(opponent != null){
			System.out.println(message.getOpponent() + message.getType()+message.userInfo.username);
			opponent.clientMessage.setMessage(chat);
			opponent.clientMessage.setType("Receive Chat");
			try {
				opponent.out.writeObject(opponent.clientMessage);
			} catch (Exception e) {}
		}
	}

	private boolean isAlNum(String s){
		return s.matches("[a-zA-Z0-9]+");
	}

	private ClientThread findUser(String username){
		for(ClientThread client : clients){
			if(username.equals(client.userInfo.username)){
				return client;
			}
		}
		System.out.println("user not found");
		return null;
	}

	public class TheServer extends Thread{

		public void run() {
			try(ServerSocket mysocket = new ServerSocket(5555);){
		    System.out.println("Server is waiting for a client!");

		    while(true) {
				ClientThread c = new ClientThread(mysocket.accept(), count);
				callback.accept("client has connected to server: " + "client #" + count);
				clients.add(c);
				c.start();

				count++;
			    }
			} catch(Exception e) {
				callback.accept("Server socket did not launch");
				}
			}
		}

		class ClientThread extends Thread{
			Socket connection;
			int count;
			ObjectInputStream in;
			ObjectOutputStream out;
			UserInfo userInfo = new UserInfo();
			Message clientMessage = new Message();

			ClientThread(Socket s, int count){
				this.connection = s;
				this.count = count;
			}

			public void updateClients(Message message, ClientThread client) throws IOException {
//				System.out.println(message);
//				if(message.recipient == 0) {
//					for (ClientThread client : clients) {
//						if (this != client) {
//							client.send(message);
//						}
//					}
//				} else {
//					clients.get(message.recipient).send(message);
//				}

//				for(int i = 0; i < clients.size(); i++) {
//					ClientThread t = clients.get(i);
//					try {
//						t.out.writeObject(message);
//					}
//					catch(Exception e) {}
//				}

				if(message.getType().equals("Login")){
					message.setLoginCheck(checkLogin(message));
					if (message.isLoginCheck()) {
						client.userInfo = users.get(message.userInfo.username);
						message.userInfo = users.get(message.userInfo.username);
					}
				} else if (message.getType().equals("Sign Up")) {
					message.setLoginCheck(checkSignUp(message));
				} else if (message.getType().equals("Random Game Start")) {
					randomMatchMaking.add(client);
				} else if (message.getType().equals("Send Chat")) {
					sendChat(message);
				}

				message.allUsers = getAllUsers();
				message.users = users;
				if(!pair2ClientsRandomly() || !message.getType().equals("Send Chat")) {
					client.clientMessage = message;
					try {
						System.out.println(client.clientMessage.toString());
						client.out.writeObject(message);
					}
					catch(Exception e) {}
				}

			}

//			public void send(Message message){
//                try {
//                    out.writeObject(message);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
			public void run(){
				try {
					in = new ObjectInputStream(connection.getInputStream());
					out = new ObjectOutputStream(connection.getOutputStream());
					connection.setTcpNoDelay(true);
				}
				catch(Exception e) {
					System.err.println("Streams not open");
				}

//				updateClients("new client on server: client #"+count);

				 while(true) {
					    try {
							Message data = (Message) in.readObject();
							updateClients(data, this);


							String STATUS = data.getType();
							System.out.println(STATUS);
							switch (STATUS) {
								case "Login":
									if(data.isLoginCheck()) {
										callback.accept("client #" + count + " logged in as: " + data.userInfo.username);
									}
									else {
										callback.accept("client #" + count + " failed to login");
									}
									break;

								case "Sign Up":
									if(data.isLoginCheck()) {
										callback.accept("client #" + count + " created account: " + data.userInfo.username);
									}
									else {
										callback.accept("client #" + count + " failed to create an account");
									}
									break;

								case "Logout":
									callback.accept(data.userInfo.username + " logged out from server");

									break;

								case "Friends":
									callback.accept(data.userInfo.username + " has requested friend details");
									break;

								case "High Scores":
									callback.accept(data.userInfo.username + " requested high score details");
									break;

								case "Win":
									callback.accept(data.userInfo.username + " won the game");

									break;

								case "Lose":
									callback.accept(data.userInfo.username + " lost the game");
									break;

								case "Send Chat":
									callback.accept(data.userInfo.username + " sent: " + data.toString() + " to: " + data.getOpponent());

									break;
								case "Receive Chat":
//									callback.accept(data.userInfo.username + " sent: " + data.toString() + " to: " + data.getOpponent());
									break;

								case "Move":
									callback.accept(data.userInfo.username + " moved");

									break;

								case "Random Game Start":
									callback.accept(data.userInfo.username + " is waiting for an opponent");

									break;

								case "Paired":
									callback.accept(data.userInfo.username + " is paired with " + data.getOpponent());

									break;

								case "Server Game Start":
									callback.accept(data.userInfo.username + " started a game with the server");

									break;

								default:
									callback.accept("client: " + count + " connected to server ");
									break;
							}
//							updateClients("client #"+count+" said: " + data);
					    	}
					    catch(Exception e) {
							e.printStackTrace();
							callback.accept("OOOOPPs...Something wrong with the socket from client: " + count + "....closing down!");
//							updateClients("Client #"+count+" has left the server!");
							clients.remove(this);
							break;
					    }
				 }
			}//end of run


		}//end of client thread
}






