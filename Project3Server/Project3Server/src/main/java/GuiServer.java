
import java.util.HashMap;

import javafx.application.Application;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GuiServer extends Application{
	Server serverConnection;

	ListView<String> listItems;
	HashMap<String, Scene> sceneMap;

	public static void main(String[] args) {
		launch(args);

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		serverConnection = new Server(data -> {
			Platform.runLater(()->{
				String STATUS = data.getType();
				System.out.println("Status: " + STATUS);
				switch (STATUS) {
					case "Login":
						if(data.isLoginCheck()) {
							listItems.getItems().add("client #" + data.getIndex() + " logged in as: " + data.userInfo.username);
						}
						else {
							listItems.getItems().add("client #" + data.getIndex() + " failed to login");
						}
						break;

					case "Sign Up":
						if(data.isLoginCheck()) {
							listItems.getItems().add("client #" + data.getIndex() + " created account: " + data.userInfo.username);
						}
						else {
							listItems.getItems().add("client #" + data.getIndex() + " failed to create an account");
						}
						break;

					case "Logout":
						listItems.getItems().add(data.userInfo.username + " logged out of account");

						break;

					case "Friends":
						listItems.getItems().add(data.userInfo.username + " has requested friend details");
						break;

					case "High Scores":
						listItems.getItems().add(data.userInfo.username + " requested high score details");
						break;

					case "Win":
						listItems.getItems().add(data.userInfo.username + " won the game");

						break;

					case "Lose":
						listItems.getItems().add(data.userInfo.username + " lost the game");
						break;

					case "Send Chat":
						listItems.getItems().add(data.userInfo.username + " sent: " + data.getMessage() + " to: " + data.getOpponent());

						break;

					case "Friend Request":
						listItems.getItems().add(data.userInfo.username + " has requested friend request");

						break;

					case "Friend Request Response":
						if (data.getMessage().toLowerCase().equals("accept")) {
						listItems.getItems().add(data.userInfo.username + " accepted the friend request from " + data.getOpponent());

						}

						else if (data.getMessage().toLowerCase().equals("decline")) {
						listItems.getItems().add(data.userInfo.username + " declined the friend request from " + data.getOpponent());

						}
						break;

					case "Receive Chat":
//									callback.accept(data.userInfo.username + " sent: " + data.getMessage() + " to: " + data.getOpponent());
						break;

					case "Move":
						listItems.getItems().add(data.userInfo.username + " moved");

						break;

					case "Random Game Start":
						listItems.getItems().add(data.userInfo.username + " is waiting for an opponent");

						break;

					case "Paired":
						listItems.getItems().add(data.userInfo.username + " is paired with " + data.getOpponent());

						break;

					case "Server Game Start":
						listItems.getItems().add(data.userInfo.username + " started a game with the server");

						break;
					case "Disconnect":
						if(data.userInfo.username == null || data.userInfo.username.isEmpty()) {
							listItems.getItems().add("client #" + data.getIndex() + " logged out of server");
						}
						else {
							listItems.getItems().add(data.userInfo.username + " disconnected from server");
						}

						break;
					case "":
						listItems.getItems().add("client #" + (serverConnection.count - 1) + " connected to server");

						break;
					default:
//						listItems.getItems().add("client #" + serverConnection.count + " connected to server");
						break;
				}
			});
		});

		listItems = new ListView<String>();

		sceneMap = new HashMap<String, Scene>();

		sceneMap.put("server",  createServerGui());

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent t) {
				serverConnection.saveUserData();
				Platform.exit();
				System.exit(0);
			}
		});

		primaryStage.setScene(sceneMap.get("server"));
		primaryStage.setTitle("This is the Server");
		primaryStage.show();
		
	}

	public Scene createServerGui() {

		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(70));
		pane.setStyle("-fx-background-color: coral");

		pane.setCenter(listItems);
		pane.setStyle("-fx-font-family: 'serif'");
		return new Scene(pane, 500, 800);


	}
}
