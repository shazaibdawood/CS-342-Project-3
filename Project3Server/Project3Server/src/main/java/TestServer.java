import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TestServer {
    public void serverCode() throws IOException, ClassNotFoundException {
        System.out.println("Started Server");
        ServerSocket mysocket = new ServerSocket(5555);

        Socket connection = mysocket.accept();

        ObjectOutputStream out = new ObjectOutputStream(connection.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(connection.getInputStream());
        connection.setTcpNoDelay(true);

        try {
            while (true) {
                String data = in.readObject().toString();
                System.out.println(data);
                out.writeObject(data.replace('a', 'b'));
            }
        } catch (EOFException eof){
            System.out.println("Connection Closed");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
