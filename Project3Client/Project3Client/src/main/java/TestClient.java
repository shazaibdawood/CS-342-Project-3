import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class TestClient {
    public void clientCode() throws IOException, ClassNotFoundException {
        System.out.println("Started Client");
        Socket socketClient = new Socket("127.0.0.1", 5555);

        ObjectOutputStream out = new ObjectOutputStream(socketClient.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socketClient.getInputStream());
        socketClient.setTcpNoDelay(true);

        String[] toSend = {"apple", "pear", "peach"};

        for(String s : toSend) {
            out.writeObject(s);
            String data = in.readObject().toString();
            System.out.println(data);
        }

        socketClient.close();

    }
}
