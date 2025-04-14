import java.io.IOException;

public class Testing {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
//        Server server = new Server();
        TestClient client = new TestClient();

//        server.serverCode();
        client.clientCode();
    }
}
