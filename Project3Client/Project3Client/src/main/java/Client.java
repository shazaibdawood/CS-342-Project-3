import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.HashMap;
import java.util.function.Consumer;



public class Client extends Thread{
	private final Object lockingObject = new Object();
	private Message responseMessage = new Message();

	Socket socketClient;

	ObjectOutputStream out;
	ObjectInputStream in;

	private Consumer<Serializable> callback;

	Client(Consumer<Serializable> call){
		callback = call;
	}

	public void run() {
		try {
			socketClient= new Socket("127.0.0.1",5555);
	    	out = new ObjectOutputStream(socketClient.getOutputStream());
	    	in = new ObjectInputStream (socketClient.getInputStream());
	   	 	socketClient.setTcpNoDelay(true);
		}
		catch(Exception e) {}

		while(true) {
			try {
				Message message = (Message) in.readObject();

				synchronized(lockingObject) {
					responseMessage = message;
					lockingObject.notify();
				}

				String STATUS = message.getType();
				switch(STATUS) {
					case "Send Chat":
						callback.accept(message.userInfo.username + ": " + message.toString());
						break;
					case "Receive Chat":
						callback.accept(message.opponent + ": " + message.toString());
						break;
				}
				System.out.println("Received " + STATUS + ": " + message.userInfo.username + " " + message.toString() + " " + message.getOpponent());
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
    }

	public void send(Message data) {
		try {
			out.writeObject(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Message sendAndWait(Message data) {
		System.out.println("Sent " + data.getType() + ": " + data.userInfo.username + " "  + data.toString() + " " + data.getOpponent());

		try {
			synchronized(lockingObject) {
				out.writeObject(data);
				lockingObject.wait();
				return responseMessage;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return responseMessage;
		}
	}
}
