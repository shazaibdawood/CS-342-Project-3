import java.util.HashMap;
import java.util.Scanner;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javafx.scene.text.Text;

public class GuiClient extends Application{
	public static Message user = new Message();

	HBox TITLE_SCREEN_BOX = getTITLE_SCREEN_BOX();

	TextField username;
	TextField password;
	Button loginButton;
	Button signUpButton;

	TextField c1;
	Button b1;
	HashMap<String, Pane> sceneMap;
	VBox clientBox;
	Client clientConnection;

	ListView<String> listItems2;
	
	public static void main(String[] args) {
//		Client clientThread = new Client();
//		clientThread.start();
//		Scanner s = new Scanner(System.in);
//		while (s.hasNext()){
//			String x = s.nextLine();
//			clientThread.send(new Message(x));
//		}

		launch(args);


	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		clientConnection = new Client(data->{
			Platform.runLater(()->{listItems2.getItems().add(data.toString());
			});
		});

		clientConnection.start();

		listItems2 = new ListView<String>();

		BorderPane masterPane = new BorderPane();
		masterPane.setCenter(drawLoginScreen());


		c1 = new TextField();
		b1 = new Button("Send");
		b1.setOnAction(e->{
			user.setMessage(c1.getText());
			user.setMove(false);
			clientConnection.send(user);
			c1.clear();
		});

		sceneMap = new HashMap<String, Pane>();

		sceneMap.put("client", createClientGui());

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent t) {
				Platform.exit();
				System.exit(0);
			}
		});

//		signUpButton.setOnAction(e->{
//			user.setLogin(true);
//			user.setMove(false);
//			clientConnection.send(user);
////			masterPane.setCenter(sceneMap.get("client"));
//		});

		loginButton.setOnAction(e->{
			user.setLogin(true);
			user.setMove(false);
			user.userInfo.username = username.getText();
			user.userInfo.password = password.getText();

			user = clientConnection.sendAndWait(user);

			if(user.isLoginCheck()){
				masterPane.setCenter(sceneMap.get("client"));
			}
			else {
				username.clear();
				password.clear();
				loginButton.setText("Invalid username or password");
			}

		});


		Scene scene = new Scene(masterPane, 700, 700);
		primaryStage.setWidth(800);
		primaryStage.setHeight(600);
		primaryStage.setResizable(false);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public Pane createClientGui() {
		clientBox = new VBox(10, c1,b1,listItems2);
		clientBox.setStyle("-fx-background-color: blue;"+"-fx-font-family: 'serif';");
		return new Pane(clientBox);
	}

	private HBox getTITLE_SCREEN_BOX(){
		Text CONNECT4_LABEL = new Text("CONNECT 4");
		CONNECT4_LABEL.setFont(new Font("Serif", 80));
		CONNECT4_LABEL.setTextAlignment(TextAlignment.CENTER);

		HBox GAME_TITLE_BOX = new HBox(10, CONNECT4_LABEL);
		GAME_TITLE_BOX.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
		GAME_TITLE_BOX.setAlignment(Pos.CENTER);

		return GAME_TITLE_BOX;
	}

	public Pane drawLoginScreen() {
		Text LOGIN_LABEL = new Text("Login");
		LOGIN_LABEL.setFont(new Font("Serif", 20));
		LOGIN_LABEL.setTextAlignment(TextAlignment.LEFT);
		HBox LOGIN_LABEL_BOX = new HBox(LOGIN_LABEL);

		username = new TextField();
		username.setPromptText("Username:");
		password = new TextField();
		password.setPromptText("Password:");

		loginButton = new Button("Login");
		loginButton.setPrefWidth(400);

		signUpButton = new Button("Sign Up");

		VBox loginScreenInfoBox = new VBox(10, LOGIN_LABEL_BOX, username, password, loginButton, signUpButton);
		loginScreenInfoBox.setMaxWidth(400);
		loginScreenInfoBox.setAlignment(Pos.CENTER);

		HBox spacer = new HBox(10);
		spacer.setMinHeight(40);

		VBox loginScreenBox = new VBox(10, TITLE_SCREEN_BOX, spacer, loginScreenInfoBox);
		loginScreenBox.setAlignment(Pos.TOP_CENTER);
		loginScreenBox.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

		BorderPane loginScreen = new BorderPane(loginScreenBox);
		loginScreen.setPadding(new Insets(10));
		loginScreen.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));

		return loginScreen;
	}

	public Pane drawSignUpScreen() {
		Text SIGN_UP_LABEL = new Text("Sign Up");
		SIGN_UP_LABEL.setFont(new Font("Serif", 20));
		SIGN_UP_LABEL.setTextAlignment(TextAlignment.LEFT);
		HBox SIGN_UP_LABEL_BOX = new HBox(SIGN_UP_LABEL);

		username = new TextField();
		username.setPromptText("Username:");
		password = new TextField();
		password.setPromptText("Password:");

		loginButton = new Button("Login");
		loginButton.setPrefWidth(400);

		signUpButton = new Button("Sign Up");

		VBox loginScreenInfoBox = new VBox(10, LOGIN_LABEL_BOX, username, password, loginButton, signUpButton);
		loginScreenInfoBox.setMaxWidth(400);
		loginScreenInfoBox.setAlignment(Pos.CENTER);

		HBox spacer = new HBox(10);
		spacer.setMinHeight(40);

		VBox loginScreenBox = new VBox(10, TITLE_SCREEN_BOX, spacer, loginScreenInfoBox);
		loginScreenBox.setAlignment(Pos.TOP_CENTER);
		loginScreenBox.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

		BorderPane loginScreen = new BorderPane(loginScreenBox);
		loginScreen.setPadding(new Insets(10));
		loginScreen.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));

		return loginScreen;
	}
}
