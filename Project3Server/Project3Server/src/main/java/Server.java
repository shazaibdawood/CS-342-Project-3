import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;
import java.io.FileWriter;
import java.io.File;
import java.util.Scanner;


import javafx.application.Platform;
import javafx.scene.control.ListView;
//import jdk.internal.org.objectweb.asm.tree.analysis.Value;


public class Server{

	int count = 1;	
	ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
	TheServer server;
	private Consumer<Serializable> callback;

	HashMap<String, UserInfo> users = new HashMap<>();

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

				if(formattedData.length == 3 && !formattedData[2].isEmpty()){
					String[] friends = formattedData[2].split(";");
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

	public void saveUserData() {
		try {
			FileWriter myWriter = new FileWriter("userinfo.txt");
			for(UserInfo u : users.values()) {
				myWriter.write(u.username + "," + u.password + "," + String.join(";", u.friends) + "\n");
			}
			myWriter.close();
		} catch (IOException e) {
			System.out.println("Error saving user info");
			e.printStackTrace();
		}
	}

	public boolean checkLogin(Message message) {
		System.out.println(message.userInfo.username + "," + message.userInfo.password);
		for(String user : users.keySet()){
			System.out.println(user);
		}
		if(users.containsKey(message.userInfo.username)){
			return (users.get(message.userInfo.username).password.equals(message.userInfo.password));
		}
		return false;
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
			
			ClientThread(Socket s, int count){
				this.connection = s;
				this.count = count;	
			}

			public void updateClients(Message message, ClientThread client) {
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

				if(message.isLogin()){
					message.setLoginCheck(checkLogin(message));
					System.out.println(message.isLoginCheck());
				}


				try {
						client.out.writeObject(message);
					}
					catch(Exception e) {}
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
							callback.accept("client: " + count + " sent: " + data);
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


	
	

	
