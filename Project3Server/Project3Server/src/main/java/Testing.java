import java.io.IOException;

public class Testing {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        TestServer server = new TestServer();
//        Client client = new Client();

        server.serverCode();
//        client.clientCode();
    }
}
