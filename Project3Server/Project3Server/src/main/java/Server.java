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

			Message message2 = new Message();
			message2.userInfo = p2.clientMessage.userInfo;
			message2.setOpponent(p1.clientMessage.userInfo.username);
			message2.setType("Paired");
			message2.allUsers = getAllUsers();
			message2.users = users;
			message2.setIndex(p2.count);

			try {
				p1.out.writeObject(message1);
				p2.out.writeObject(message2);
				callback.accept(message1);
				callback.accept(message2);
				p1.clientMessage = message1;
				p2.clientMessage = message2;
				updateClientMessage(message1);
				updateClientMessage(message2);

//				System.out.println("Pairing clients:");
//				System.out.println(message1.toString());
//				System.out.println(message2.toString());

				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return false;
	}
	private void updateClientMessage(Message message){
		for(ClientThread client : clients){
			if(client.clientMessage.userInfo.username.equals(message.userInfo.username)){
				client.clientMessage = message;
				return;
			}
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

			Message message2 = new Message();
			message2.userInfo = message.userInfo;
			message2.setOpponent(message.getOpponent());
			message2.setType("Send Chat");
			message2.allUsers = getAllUsers();
			message2.users = users;
			message2.setIndex(message.getIndex());
			message2.setMessage(chat);
			try {
				opponent.out.writeObject(message1);
				opponent.clientMessage = message1;

				sender.out.writeObject(message2);
				sender.clientMessage = message2;

				updateClientMessage(message1);
				updateClientMessage(message2);
			} catch (Exception e) {}
		}
	}

	public void handleFriendRequest(Message message) {
		ClientThread sender = findUser(message.userInfo.username);
		ClientThread target = findUser(message.getOpponent());

		if (message.getType().equals("Friend Request")) {
			if (target != null) {
				String targetName = target.clientMessage.userInfo.username;
				Message request = new Message();
				request.userInfo = sender.clientMessage.userInfo;
				request.setOpponent(targetName);
				request.setType("Friend Request Incoming");
				request.allUsers = getAllUsers();
				request.users = users;
				request.setIndex(target.count);
				request.setMessage(sender.clientMessage.userInfo.username + " sent you a friend request!");

				try {
					target.out.writeObject(request);
					target.clientMessage = request;
					updateClientMessage(request);

					Message confirm = new Message();
					confirm.userInfo = sender.clientMessage.userInfo;
					confirm.setOpponent(targetName);
					confirm.setType("Send Chat");
					confirm.allUsers = getAllUsers();
					confirm.users = users;
					confirm.setIndex(sender.count);
					confirm.setMessage("Friend request sent to " + targetName + ".");

					sender.out.writeObject(confirm);
					sender.clientMessage = confirm;
					updateClientMessage(confirm);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		else if (message.getType().equals("Friend Request Response")) {
			String response = message.getMessage().toLowerCase();
			String requesterName = message.getOpponent();
			String responderName = message.userInfo.username;

			ClientThread requesterClient = findUser(requesterName);
			ClientThread responderClient = findUser(responderName);

			if (response.equals("accept")) {
				if (users.containsKey(requesterName) && users.containsKey(responderName)) {
					users.get(requesterName).friends.add(responderName);
					users.get(responderName).friends.add(requesterName);
					saveUserData();

					if (requesterClient != null) {
						sendFriendListUpdate(requesterClient);
					}
					if (responderClient != null) {
						sendFriendListUpdate(responderClient);
					}
				}
			}
		}
	}

	private void sendFriendListUpdate(ClientThread client) {
		try {
			Message updated = new Message();
			updated.setType("Friend List Updated");
			updated.userInfo = users.get(client.clientMessage.userInfo.username);
			updated.allUsers = getAllUsers();
			updated.users = users;
			updated.setOpponent(client.clientMessage.getOpponent());
			client.out.writeObject(updated);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
			System.out.println(client.clientMessage.toString());
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
				} else if (message.getType().equals("Friend Request") || message.getType().equals("Friend Request Response")) {
					handleFriendRequest(message);
				} else if(message.getType().equals("Friend Request Response")) {
					System.out.println(message.userInfo.username + " responded '" + message.getMessage() + "' to friend request from " + message.getOpponent());
				}

				message.allUsers = getAllUsers();
				message.users = users;
				client.clientMessage = message;
//				System.out.println("Client: " + client.clientMessage.userInfo.username + " " + client.clientMessage.getOpponent() + " " + client.clientMessage.getType());
				boolean paired = pair2ClientsRandomly();

				if((!paired || message.getType().equals("Friends") )&& !message.getType().equals("Send Chat") && !message.getType().equals("Paired")){
					try {
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
							callback.accept(data);

							updateClients(data, this);
							printClients();
//							System.out.println("Count: " + count);
//							String STATUS = data.getType();
//							System.out.println(STATUS);
//							switch (STATUS) {
//								case "Login":
//									if(data.isLoginCheck()) {
////										callback.accept("client #" + count + " logged in as: " + data.userInfo.username);
//									}
//									else {
//										callback.accept("client #" + count + " failed to login");
//									}
//									break;
//
//								case "Sign Up":
//									if(data.isLoginCheck()) {
//										callback.accept("client #" + count + " created account: " + data.userInfo.username);
//									}
//									else {
//										callback.accept("client #" + count + " failed to create an account");
//									}
//									break;
//
//								case "Logout":
//									callback.accept(data.userInfo.username + " logged out from server");
//
//									break;
//
//								case "Friends":
//									callback.accept(data.userInfo.username + " has requested friend details");
//									break;
//
//								case "High Scores":
//									callback.accept(data.userInfo.username + " requested high score details");
//									break;
//
//								case "Win":
//									callback.accept(data.userInfo.username + " won the game");
//
//									break;
//
//								case "Lose":
//									callback.accept(data.userInfo.username + " lost the game");
//									break;
//
//								case "Send Chat":
//									callback.accept(data.userInfo.username + " sent: " + data.toString() + " to: " + data.getOpponent());
//
//									break;
//								case "Receive Chat":
////									callback.accept(data.userInfo.username + " sent: " + data.toString() + " to: " + data.getOpponent());
//									break;
//
//								case "Move":
//									callback.accept(data.userInfo.username + " moved");
//
//									break;
//
//								case "Random Game Start":
//									callback.accept(data.userInfo.username + " is waiting for an opponent");
//
//									break;
//
//								case "Paired":
//									callback.accept(data.userInfo.username + " is paired with " + data.getOpponent());
//
//									break;
//
//								case "Server Game Start":
//									callback.accept(data.userInfo.username + " started a game with the server");
//
//									break;
//
//								default:
//									callback.accept("client: " + count + " connected to server ");
//									break;
//							}
//							updateClients("client #"+count+" said: " + data);

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






