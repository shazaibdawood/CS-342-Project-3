import java.util.*;

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
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javafx.scene.text.Text;

import static java.lang.Math.max;

public class GuiClient extends Application{
	public static Message user = new Message();
	private String pendingFriendRequest = null;
	HBox TITLE_SCREEN_BOX = getTITLE_SCREEN_BOX();

	TextField username = new TextField();
	TextField password = new TextField();
	TextField message = new TextField();

	Button loginButton = new Button("Login");
	Button signUpButton = new Button("Sign Up");
	Button createAccountButton = new Button("Create Account");
	Button PVPButton = new Button("PVP");
	Button PVEButton = new Button("PVE");
	Button highScoreButton = new Button("High Scores");
	Button logoutButton = new Button("Logout");
	Button friendsButton = new Button("Friends");
	Button backButton = new Button("Back");
	Button sendButton = new Button("Send");
	Button quitButton = new Button("Quit");
	Button friendRequestButton = new Button("Friend Request");
	Button sendMoveButton = new Button("Send Move");
	Button clearMoveButton = new Button("Clear Move");

	Button column1Button = new Button("Place");
	Button column2Button = new Button("Place");
	Button column3Button = new Button("Place");
	Button column4Button = new Button("Place");
	Button column5Button = new Button("Place");
	Button column6Button = new Button("Place");
	Button column7Button = new Button("Place");

	int[][] tempBoard = user.getBoard();
	boolean finalizedMove = false;
	String gameStatus = "Continue";
	boolean gameFinished = false;

	Client clientConnection;

	ListView<String> chatLogs = new ListView<String>();

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
		BorderPane masterPane = new BorderPane();
		masterPane.setCenter(drawLoginScreen());
		column1Button.setDisable(true);
		column2Button.setDisable(true);
		column3Button.setDisable(true);
		column4Button.setDisable(true);
		column5Button.setDisable(true);
		column6Button.setDisable(true);
		column7Button.setDisable(true);

		clientConnection = new Client(data->{
//			Platform.runLater(()->{
//				chatLogs.getItems().add(data.toString());
//				System.out.println(data.toString());
//
//				Message test = (Message)data;
//				System.out.println(test.getType());
////
////				System.out.println(test.getType());
		// });

			Platform.runLater(()->{
				user = data;
				user.userInfo = data.userInfo;
				String STATUS = data.getType();
				System.out.println(STATUS);
				switch(STATUS){
					case "Send Chat":
						chatLogs.getItems().add(data.userInfo.username + ": " + data.getMessage());
						break;
					case "Receive Chat":
						chatLogs.getItems().add(data.opponent + ": " + data.getMessage());
						break;
					case "Paired":
//						clientConnection.send(user);
						finalizedMove = true;
						gameFinished = false;
						sendMoveButton.setDisable(true);
						chatLogs.getItems().add(data.userInfo.username + " is paired with " + data.getOpponent());
						if(user.isLoginCheck()){
							chatLogs.getItems().add(data.userInfo.username + " moves first");
						}
						else {
							chatLogs.getItems().add(data.getOpponent() + " moves first");
						}
						masterPane.setCenter(drawGameScreen());
						break;
					case "Receive Move":
						chatLogs.getItems().add(data.opponent + " made a move");
						tempBoard = user.getBoard();
						finalizedMove = true;
						masterPane.setCenter(drawGameScreen());
						break;

					case "Win":
						chatLogs.getItems().add(data.userInfo.username + " won");
						tempBoard = user.getBoard();
						finalizedMove = true;
						gameStatus = "Win";
						gameFinished = true;
						quitButton.setDisable(false);
						masterPane.setCenter(drawGameScreen());
						break;

					case "Lose":
						chatLogs.getItems().add(data.userInfo.username + " lost");
						tempBoard = user.getBoard();
						finalizedMove = true;
						gameFinished = true;
						gameStatus = "Lose";
						quitButton.setDisable(false);
						masterPane.setCenter(drawGameScreen());
						break;
					case "Draw":
						chatLogs.getItems().add("Tie game");
						tempBoard = user.getBoard();
						finalizedMove = true;
						gameFinished = true;
						gameStatus = "Draw";
						quitButton.setDisable(false);
						masterPane.setCenter(drawGameScreen());
						break;
					case "Quit":
						gameStatus = "Continue";
						masterPane.setCenter(drawWelcomeScreen());
						break;
					case "Exit":
						gameStatus = "Continue";
						masterPane.setCenter(drawWelcomeScreen());
						break;
					case "Friend Request":
						sendMoveButton.setDisable(true);
						clearMoveButton.setDisable(true);
						column1Button.setDisable(true);
						column2Button.setDisable(true);
						column3Button.setDisable(true);
						column4Button.setDisable(true);
						column5Button.setDisable(true);
						column6Button.setDisable(true);
						column7Button.setDisable(true);
						break;
					case "Friend Request Incoming":
						pendingFriendRequest = data.userInfo.username;
						chatLogs.getItems().add("You received a friend request from " + pendingFriendRequest + ". Type 'accept' or 'decline'.");
						break;
					case "Friend List Updated":
						user.userInfo = data.userInfo;
						user.allUsers = data.allUsers;
						user.users    = data.users;
						chatLogs.getItems().add("Friend list updated!");
						break;

					default:
						break;

				}
			});
			});

		clientConnection.start();


//		masterPane.setCenter(drawWelcomeScreen());
//		masterPane.setCenter(drawSignUpScreen());

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent t) {
				user.setType("Disconnect");
				clientConnection.send(user);
				Platform.exit();
				System.exit(0);
			}
		});

		loginButton.setOnAction(e->{
			user.setLoginCheck(true);
			user.userInfo.username = username.getText();
			user.userInfo.password = password.getText();
			user.setType("Login");
			user = clientConnection.sendAndWait(user);

			username.clear();
			password.clear();

			if(user.isLoginCheck()){
				primaryStage.setTitle(user.userInfo.username);
				masterPane.setCenter(drawWelcomeScreen());
			}
			else {
				masterPane.setCenter(drawLoginScreen());
			}
		});

		signUpButton.setOnAction(e->{
			username.clear();
			password.clear();

			masterPane.setCenter(drawSignUpScreen());
		});

		createAccountButton.setOnAction(e->{
			user.setLoginCheck(true);
			user.setType("Sign Up");
			user.userInfo.username = username.getText();
			user.userInfo.password = password.getText();

			user = clientConnection.sendAndWait(user);

			username.clear();
			password.clear();

			if(user.isLoginCheck()){
				masterPane.setCenter(drawLoginScreen());
			}
			else {
				masterPane.setCenter(drawSignUpScreen());
			}
		});

		logoutButton.setOnAction(e->{
			user.setType("Logout");
			clientConnection.send(user);
			user = new Message();
			masterPane.setCenter(drawLoginScreen());
		});

		backButton.setOnAction(e->{
			masterPane.setCenter(drawWelcomeScreen());
		});

		friendsButton.setOnAction(e->{
			user.setType("Friends");
			user = clientConnection.sendAndWait(user);

			masterPane.setCenter(drawFriendsScreen());
		});

		highScoreButton.setOnAction(e->{
			user.setType("High Scores");

			user = clientConnection.sendAndWait(user);

			masterPane.setCenter(drawHighScoreScreen());
		});

		PVPButton.setOnAction(e->{
			user.setType("Random Game Start");
			user.setOpponent("");

			clientConnection.send(user);

			masterPane.setCenter(drawGameScreen());
		});

		PVEButton.setOnAction(e->{
			user.setType("Server Game Start");
			user.setOpponent("SERVER");

			clientConnection.send(user);
			masterPane.setCenter(drawGameScreen());
		});

		sendButton.setOnAction(e -> {
			String txt = message.getText();
			if (txt.isEmpty()) return;

			if (pendingFriendRequest != null &&
					(txt.equalsIgnoreCase("accept") || txt.equalsIgnoreCase("decline"))) {

				Message rsp = new Message();
				rsp.userInfo = user.userInfo;
				rsp.setType("Friend Request Response");
				rsp.setOpponent(pendingFriendRequest);
				rsp.setMessage(txt.toLowerCase());
				clientConnection.send(rsp);
				pendingFriendRequest = null;

			} else {
				Message chat = new Message();
				chat.userInfo = user.userInfo;
				chat.setType("Send Chat");
				chat.setOpponent(user.getOpponent());
				chat.setMessage(txt);
				clientConnection.send(chat);
			}
			message.clear();
		});


		clearMoveButton.setOnAction(e->{
			tempBoard = user.getBoard();
			finalizedMove = true;
			sendMoveButton.setDisable(true);
			masterPane.setCenter(drawGameScreen());
		});

		sendMoveButton.setOnAction(e->{
			user.setBoard(tempBoard);
			user.setType("Send Move");
			chatLogs.getItems().add(user.userInfo.username + " made a move");
			clientConnection.send(user);

			column1Button.setDisable(true);
			column2Button.setDisable(true);
			column3Button.setDisable(true);
			column4Button.setDisable(true);
			column5Button.setDisable(true);
			column6Button.setDisable(true);
			column7Button.setDisable(true);
			sendMoveButton.setDisable(true);
			clearMoveButton.setDisable(true);
		});

		quitButton.setOnAction(e->{
			if(!gameFinished){
				chatLogs.getItems().clear();
				user.setType("Quit");
				clientConnection.send(user);
			}
			else {
				user.setType("Exit");
				clientConnection.send(user);
				chatLogs.getItems().clear();
			}

		});

//		friendRequestButton.setOnAction(e->{
//			//if (!message.getText().isEmpty()) {
//			Message request = new Message();
//			request.userInfo= user.userInfo;
//			request.setType("Friend Request");
//			request.setOpponent(user.getOpponent());
//			clientConnection.send(request);
//			chatLogs.getItems().add("Friend request sent to " + user.getOpponent() + ".");
//			message.clear();
//			//}
//		});

		column1Button.setOnAction(e->{
			finalizedMove = false;
			for(int i = 5; i >= 0; i--) {
				if(tempBoard[i][0] == 0){
					tempBoard[i][0] = 1;
					break;
				}
			}
			column1Button.setDisable(true);
			column2Button.setDisable(true);
			column3Button.setDisable(true);
			column4Button.setDisable(true);
			column5Button.setDisable(true);
			column6Button.setDisable(true);
			column7Button.setDisable(true);
			sendMoveButton.setDisable(false);

			masterPane.setCenter(drawGameScreen());
		});

		column2Button.setOnAction(e->{
			finalizedMove = false;
			for(int i = 5; i >= 0; i--) {
				if(tempBoard[i][1] == 0){
					tempBoard[i][1] = 1;
					break;
				}
			}
			column1Button.setDisable(true);
			column2Button.setDisable(true);
			column3Button.setDisable(true);
			column4Button.setDisable(true);
			column5Button.setDisable(true);
			column6Button.setDisable(true);
			column7Button.setDisable(true);
			sendMoveButton.setDisable(false);

			masterPane.setCenter(drawGameScreen());
		});

		column3Button.setOnAction(e->{
			finalizedMove = false;
			for(int i = 5; i >= 0; i--) {
				if(tempBoard[i][2] == 0){
					tempBoard[i][2] = 1;
					break;
				}
			}
			column1Button.setDisable(true);
			column2Button.setDisable(true);
			column3Button.setDisable(true);
			column4Button.setDisable(true);
			column5Button.setDisable(true);
			column6Button.setDisable(true);
			column7Button.setDisable(true);
			sendMoveButton.setDisable(false);

			masterPane.setCenter(drawGameScreen());
		});

		column4Button.setOnAction(e->{
			finalizedMove = false;
			for(int i = 5; i >= 0; i--) {
				if(tempBoard[i][3] == 0){
					tempBoard[i][3] = 1;
					break;
				}
			}
			column1Button.setDisable(true);
			column2Button.setDisable(true);
			column3Button.setDisable(true);
			column4Button.setDisable(true);
			column5Button.setDisable(true);
			column6Button.setDisable(true);
			column7Button.setDisable(true);
			sendMoveButton.setDisable(false);

			masterPane.setCenter(drawGameScreen());
		});

		column5Button.setOnAction(e->{
			finalizedMove = false;
			for(int i = 5; i >= 0; i--) {
				if(tempBoard[i][4] == 0){
					tempBoard[i][4] = 1;
					break;
				}
			}
			column1Button.setDisable(true);
			column2Button.setDisable(true);
			column3Button.setDisable(true);
			column4Button.setDisable(true);
			column5Button.setDisable(true);
			column6Button.setDisable(true);
			column7Button.setDisable(true);
			sendMoveButton.setDisable(false);

			masterPane.setCenter(drawGameScreen());
		});

		column6Button.setOnAction(e->{
			finalizedMove = false;
			for(int i = 5; i >= 0; i--) {
				if(tempBoard[i][5] == 0){
					tempBoard[i][5] = 1;
					break;
				}
			}
			column1Button.setDisable(true);
			column2Button.setDisable(true);
			column3Button.setDisable(true);
			column4Button.setDisable(true);
			column5Button.setDisable(true);
			column6Button.setDisable(true);
			column7Button.setDisable(true);
			sendMoveButton.setDisable(false);

			masterPane.setCenter(drawGameScreen());
		});

		column7Button.setOnAction(e->{
			finalizedMove = false;
			for(int i = 5; i >= 0; i--) {
				if(tempBoard[i][6] == 0){
					tempBoard[i][6] = 1;
					break;
				}
			}
			column1Button.setDisable(true);
			column2Button.setDisable(true);
			column3Button.setDisable(true);
			column4Button.setDisable(true);
			column5Button.setDisable(true);
			column6Button.setDisable(true);
			column7Button.setDisable(true);

			sendMoveButton.setDisable(false);
			masterPane.setCenter(drawGameScreen());
		});

		Scene scene = new Scene(masterPane, 700, 700);
		primaryStage.setWidth(800);
		primaryStage.setHeight(650);
		primaryStage.setResizable(false);
		primaryStage.setScene(scene);
		primaryStage.show();
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
		LOGIN_LABEL.setFont(new Font("Serif", 40));
		LOGIN_LABEL.setTextAlignment(TextAlignment.LEFT);
		HBox LOGIN_LABEL_BOX = new HBox(LOGIN_LABEL);

		username.setPromptText("Username:");
		password.setPromptText("Password:");

		loginButton.setPrefWidth(400);

		Text WARNING_LABEL = new Text(user.warning);
		WARNING_LABEL.setFont(new Font("Serif", 20));
		WARNING_LABEL.setTextAlignment(TextAlignment.LEFT);
		HBox WARNING_LABEL_BOX = new HBox(WARNING_LABEL);

		signUpButton.setPrefWidth(150);

		VBox loginScreenInfoBox;
		if(user.isLoginCheck()){
			loginScreenInfoBox = new VBox(10, LOGIN_LABEL_BOX, username, password, loginButton, signUpButton);
		}
		else {
			loginScreenInfoBox = new VBox(10, LOGIN_LABEL_BOX, username, password, loginButton, WARNING_LABEL_BOX, signUpButton);
		}
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
		SIGN_UP_LABEL.setFont(new Font("Serif", 40));
		SIGN_UP_LABEL.setTextAlignment(TextAlignment.LEFT);
		HBox SIGN_UP_LABEL_BOX = new HBox(SIGN_UP_LABEL);

		username.setPromptText("Username:");
		password.setPromptText("Password:");

		createAccountButton.setPrefWidth(400);

		Text WARNING_LABEL = new Text(user.warning);
		WARNING_LABEL.setFont(new Font("Serif", 20));
		WARNING_LABEL.setTextAlignment(TextAlignment.LEFT);
		HBox WARNING_LABEL_BOX = new HBox(WARNING_LABEL);

		VBox signUpScreenInfoBox;
		if(user.isLoginCheck()){
			signUpScreenInfoBox = new VBox(10, SIGN_UP_LABEL_BOX, username, password, createAccountButton);
		}
		else {
			signUpScreenInfoBox = new VBox(10, SIGN_UP_LABEL_BOX, username, password, createAccountButton, WARNING_LABEL_BOX);
		}

		signUpScreenInfoBox.setMaxWidth(400);
		signUpScreenInfoBox.setAlignment(Pos.CENTER);

		HBox spacer = new HBox(10);
		spacer.setMinHeight(40);

		VBox signUpScreenBox = new VBox(10, TITLE_SCREEN_BOX, spacer, signUpScreenInfoBox);
		signUpScreenBox.setAlignment(Pos.TOP_CENTER);
		signUpScreenBox.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

		BorderPane signUpScreen = new BorderPane(signUpScreenBox);
		signUpScreen.setPadding(new Insets(10));
		signUpScreen.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));

		return signUpScreen;
	}

	public Pane drawWelcomeScreen() {
		Text WELCOME_LABEL = new Text("Welcome " + user.userInfo.username + "!");
		WELCOME_LABEL.setFont(new Font("Serif", 50));
		WELCOME_LABEL.setTextAlignment(TextAlignment.CENTER);
		HBox WELCOME_LABEL_BOX = new HBox(WELCOME_LABEL);
		WELCOME_LABEL_BOX.setAlignment(Pos.CENTER);

		PVPButton.setPrefWidth(150);
		PVPButton.setPrefHeight(40);
		PVEButton.setPrefWidth(150);
		PVEButton.setPrefHeight(40);
		HBox GAME_SELECTION_BOX = new HBox(10, PVPButton, PVEButton);
		GAME_SELECTION_BOX.setAlignment(Pos.CENTER);

		highScoreButton.setPrefWidth(150);
		highScoreButton.setPrefHeight(25);

		logoutButton.setPrefWidth(100);
		logoutButton.setPrefHeight(25);
		friendsButton.setPrefWidth(100);
		friendsButton.setPrefHeight(25);
		Region spacer1 = new Region();
		HBox.setHgrow(spacer1, Priority.ALWAYS);
		HBox LOGOUT_FRIENDS_BOX = new HBox(10, logoutButton, spacer1, friendsButton);
		LOGOUT_FRIENDS_BOX.setAlignment(Pos.CENTER);
		LOGOUT_FRIENDS_BOX.setPadding(new Insets(10, 10, 10, 10));

		HBox spacer = new HBox(10);
		spacer.setMinHeight(40);
		Region spacer2 = new Region();
		VBox.setVgrow(spacer2, Priority.ALWAYS);
		VBox welcomeScreenBox = new VBox(10, TITLE_SCREEN_BOX, spacer, WELCOME_LABEL_BOX, GAME_SELECTION_BOX, highScoreButton, spacer2, LOGOUT_FRIENDS_BOX);
		welcomeScreenBox.setAlignment(Pos.TOP_CENTER);
		welcomeScreenBox.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

		BorderPane welcomeScreen = new BorderPane(welcomeScreenBox);
		welcomeScreen.setPadding(new Insets(10, 10, 10, 10));
		welcomeScreen.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));

		return welcomeScreen;
	}

	public Pane drawFriendsScreen() {
//		Text FRIENDS_LABEL = new Text("Friends");
//		FRIENDS_LABEL.setFont(new Font("Serif", 40));
//		FRIENDS_LABEL.setTextAlignment(TextAlignment.LEFT);
//		HBox FRIENDS_LABEL_BOX = new HBox(FRIENDS_LABEL);
//		FRIENDS_LABEL_BOX.setMaxWidth(250);

		Text FRIEND = new Text("Friend");
		FRIEND.setFont(new Font("Serif", 25));
		FRIEND.setTextAlignment(TextAlignment.LEFT);
		HBox FRIEND_BOX = new HBox(FRIEND);
		FRIEND_BOX.setAlignment(Pos.CENTER_LEFT);

		Region spacers = new Region();
		HBox.setHgrow(spacers, Priority.ALWAYS);

		Text STATUS = new Text("Status");
		STATUS.setFont(new Font("Serif", 25));
		STATUS.setTextAlignment(TextAlignment.LEFT);
		HBox STATUS_BOX = new HBox(STATUS);
		STATUS_BOX.setAlignment(Pos.CENTER_RIGHT);

		HBox FRIEND_TITLE_BOX = new HBox(10, FRIEND_BOX, spacers, STATUS_BOX);
		FRIEND_TITLE_BOX.setAlignment(Pos.CENTER);
		FRIEND_TITLE_BOX.setMaxWidth(250);

		VBox FRIENDS_LIST_BOX = new VBox(10, FRIEND_TITLE_BOX);
		for (String friend : user.userInfo.friends) {
			Text FRIEND_LABEL = new Text(friend + " is:");
			FRIEND_LABEL.setFont(new Font("Serif", 13));
			FRIEND_LABEL.setTextAlignment(TextAlignment.LEFT);
			HBox FRIEND_LABEL_BOX = new HBox(FRIEND_LABEL);
			FRIEND_LABEL_BOX.setAlignment(Pos.CENTER_LEFT);

			Region spacer1 = new Region();
			HBox.setHgrow(spacer1, Priority.ALWAYS);

			Text ONLINE_LABEL;
			if(user.allUsers.get(friend)){
				ONLINE_LABEL = new Text("online");
			}
			else {
				ONLINE_LABEL = new Text("offline");
			}
			ONLINE_LABEL.setFont(new Font("Serif", 13));
			ONLINE_LABEL.setTextAlignment(TextAlignment.RIGHT);
			HBox ONLINE_LABEL_BOX = new HBox(ONLINE_LABEL);
			ONLINE_LABEL_BOX.setAlignment(Pos.CENTER_RIGHT);

			HBox FRIEND_DETAIL_BOX = new HBox(10, FRIEND_LABEL_BOX, spacer1, ONLINE_LABEL_BOX);
			FRIEND_DETAIL_BOX.setAlignment(Pos.CENTER);
			FRIEND_DETAIL_BOX.setMaxWidth(250);

			FRIENDS_LIST_BOX.getChildren().addAll(FRIEND_DETAIL_BOX);
		}

		if(user.userInfo.friends == null || user.userInfo.friends.isEmpty()) {
			Text NO_FRIENDS_LABEL = new Text("No friends. Add friends during game.");
			NO_FRIENDS_LABEL.setFont(new Font("Serif", 25));
			NO_FRIENDS_LABEL.setTextAlignment(TextAlignment.LEFT);
			HBox NO_FRIENDS_LABEL_BOX = new HBox(NO_FRIENDS_LABEL);
			NO_FRIENDS_LABEL_BOX.setAlignment(Pos.CENTER);

			FRIENDS_LIST_BOX.getChildren().addAll(NO_FRIENDS_LABEL_BOX);
		}
		FRIENDS_LIST_BOX.setAlignment(Pos.CENTER);

		logoutButton.setPrefWidth(100);
		logoutButton.setPrefHeight(25);
		backButton.setPrefWidth(100);
		backButton.setPrefHeight(25);
		Region spacer1 = new Region();
		HBox.setHgrow(spacer1, Priority.ALWAYS);
		HBox LOGOUT_BACK_BOX = new HBox(10, logoutButton, spacer1, backButton);
		LOGOUT_BACK_BOX.setAlignment(Pos.CENTER);
		LOGOUT_BACK_BOX.setPadding(new Insets(10, 10, 10, 10));

		HBox spacer = new HBox(10);
		spacer.setMinHeight(40);
		Region spacer2 = new Region();
		VBox.setVgrow(spacer2, Priority.ALWAYS);
		VBox welcomeScreenBox = new VBox(10, TITLE_SCREEN_BOX, FRIENDS_LIST_BOX, spacer2, LOGOUT_BACK_BOX);
		welcomeScreenBox.setAlignment(Pos.TOP_CENTER);
		welcomeScreenBox.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

		BorderPane welcomeScreen = new BorderPane(welcomeScreenBox);
		welcomeScreen.setPadding(new Insets(10, 10, 10, 10));
		welcomeScreen.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));

		return welcomeScreen;
	}

	public Pane drawHighScoreScreen() {
		Text HIGH_SCORE_LABEL = new Text("High Scores");
		HIGH_SCORE_LABEL.setFont(new Font("Serif", 40));
		HIGH_SCORE_LABEL.setTextAlignment(TextAlignment.LEFT);
		HBox HIGH_SCORE_LABEL_BOX = new HBox(HIGH_SCORE_LABEL);
		HIGH_SCORE_LABEL_BOX.setMaxWidth(400);

		TreeMap<Double, List<UserInfo>> highScoresSorted = new TreeMap<>();
		for(String player : user.users.keySet()){
			int wins = user.users.get(player).wins;
			int totalGames = Math.max(user.users.get(player).totalGames, 1);
			UserInfo u = user.users.get(player);

			double winRate = ((double) wins / totalGames) * 100;

			highScoresSorted.putIfAbsent(winRate, new ArrayList<>());
			highScoresSorted.get(winRate).add(u);
		}

		Text USER_LABEL = new Text("User");
		USER_LABEL.setFont(new Font("Serif", 20));
		USER_LABEL.setTextAlignment(TextAlignment.LEFT);
		HBox USER_LABEL_BOX = new HBox(USER_LABEL);
		USER_LABEL_BOX.setAlignment(Pos.CENTER_LEFT);
		VBox HIGH_SCORE_LIST_USERS_BOX = new VBox(10, USER_LABEL_BOX);

		Region spacer1 = new Region();
		HBox.setHgrow(spacer1, Priority.ALWAYS);

		Text WINS_LABEL = new Text("Wins");
		WINS_LABEL.setFont(new Font("Serif", 20));
		WINS_LABEL.setTextAlignment(TextAlignment.RIGHT);
		HBox WINS_LABEL_BOX = new HBox(WINS_LABEL);
		WINS_LABEL_BOX.setAlignment(Pos.CENTER_RIGHT);
		VBox HIGH_SCORE_LIST_WINS_BOX = new VBox(10, WINS_LABEL_BOX);
		HIGH_SCORE_LIST_WINS_BOX.setMinWidth(75);

		Text LOSSES_LABEL = new Text("Losses");
		LOSSES_LABEL.setFont(new Font("Serif", 20));
		LOSSES_LABEL.setTextAlignment(TextAlignment.RIGHT);
		HBox LOSSES_LABEL_BOX = new HBox(LOSSES_LABEL);
		LOSSES_LABEL_BOX.setAlignment(Pos.CENTER_RIGHT);
		VBox HIGH_SCORE_LIST_LOSSES_BOX = new VBox(10, LOSSES_LABEL_BOX);
		HIGH_SCORE_LIST_LOSSES_BOX.setMinWidth(75);

		Text DRAWS_LABEL = new Text("Draws");
		DRAWS_LABEL.setFont(new Font("Serif", 20));
		DRAWS_LABEL.setTextAlignment(TextAlignment.RIGHT);
		HBox DRAWS_LABEL_BOX = new HBox(DRAWS_LABEL);
		DRAWS_LABEL_BOX.setAlignment(Pos.CENTER_RIGHT);
		VBox HIGH_SCORE_LIST_DRAWS_BOX = new VBox(10, DRAWS_LABEL_BOX);
		HIGH_SCORE_LIST_DRAWS_BOX.setMinWidth(75);

		Text TOTAL_GAMES_LABEL = new Text("# Games");
		TOTAL_GAMES_LABEL.setFont(new Font("Serif", 20));
		TOTAL_GAMES_LABEL.setTextAlignment(TextAlignment.RIGHT);
		HBox TOTAL_GAMES_LABEL_BOX = new HBox(TOTAL_GAMES_LABEL);
		TOTAL_GAMES_LABEL_BOX.setAlignment(Pos.CENTER_RIGHT);
		VBox HIGH_SCORE_LIST_TOTAL_GAMES_BOX = new VBox(10, TOTAL_GAMES_LABEL_BOX);
		HIGH_SCORE_LIST_TOTAL_GAMES_BOX.setMinWidth(75);

		int count = 0;
		for (double score : highScoresSorted.descendingKeySet()) {
			if(count >= 10){
				break;
			}
			List<UserInfo> sameScores = highScoresSorted.get(score);

			for (UserInfo u : sameScores) {
				if(count >= 10){
					break;
				}
				int losses = (int)((double) u.losses / Math.max(u.totalGames, 1) * 100);
				int draws = (int)((double) u.draws / Math.max(u.totalGames, 1) * 100);

				Text USER = new Text((count + 1) + ". " + u.username);
				USER.setFont(new Font("Serif", 13));
				USER.setTextAlignment(TextAlignment.LEFT);
				HBox USER_BOX = new HBox(USER);
				USER_BOX.setAlignment(Pos.CENTER_LEFT);
				HIGH_SCORE_LIST_USERS_BOX.getChildren().add(USER_BOX);

				Text WINS = new Text((int)score + "%");
				WINS.setFont(new Font("Serif", 13));
				WINS.setTextAlignment(TextAlignment.LEFT);
				HBox Wins_BOX = new HBox(WINS);
				Wins_BOX.setAlignment(Pos.CENTER_RIGHT);
				HIGH_SCORE_LIST_WINS_BOX.getChildren().add(Wins_BOX);

				Text LOSS = new Text(losses + "%");
				LOSS.setFont(new Font("Serif", 13));
				LOSS.setTextAlignment(TextAlignment.LEFT);
				HBox LOSS_BOX = new HBox(LOSS);
				LOSS_BOX.setAlignment(Pos.CENTER_RIGHT);
				HIGH_SCORE_LIST_LOSSES_BOX.getChildren().add(LOSS_BOX);

				Text DRAW = new Text(draws + "%");
				DRAW.setFont(new Font("Serif", 13));
				DRAW.setTextAlignment(TextAlignment.LEFT);
				HBox DRAW_BOX = new HBox(DRAW);
				DRAW_BOX.setAlignment(Pos.CENTER_RIGHT);
				HIGH_SCORE_LIST_DRAWS_BOX.getChildren().add(DRAW_BOX);

				Text TOTAL = new Text(u.totalGames + "");
				TOTAL.setFont(new Font("Serif", 13));
				TOTAL.setTextAlignment(TextAlignment.LEFT);
				HBox TOTAL_BOX = new HBox(TOTAL);
				TOTAL_BOX.setAlignment(Pos.CENTER_RIGHT);
				HIGH_SCORE_LIST_TOTAL_GAMES_BOX.getChildren().add(TOTAL_BOX);

				count++;
			}
		}

		HBox HIGH_SCORES_INFO_LIST = new HBox(10, HIGH_SCORE_LIST_USERS_BOX, spacer1, HIGH_SCORE_LIST_WINS_BOX, HIGH_SCORE_LIST_LOSSES_BOX, HIGH_SCORE_LIST_DRAWS_BOX, HIGH_SCORE_LIST_TOTAL_GAMES_BOX);
		HIGH_SCORES_INFO_LIST.setAlignment(Pos.CENTER);
		HIGH_SCORES_INFO_LIST.setMaxWidth(400);

		logoutButton.setPrefWidth(100);
		logoutButton.setPrefHeight(25);
		backButton.setPrefWidth(100);
		backButton.setPrefHeight(25);
		Region spacer2 = new Region();
		HBox.setHgrow(spacer2, Priority.ALWAYS);
		HBox LOGOUT_BACK_BOX = new HBox(10, logoutButton, spacer2, backButton);
		LOGOUT_BACK_BOX.setAlignment(Pos.CENTER);
		LOGOUT_BACK_BOX.setPadding(new Insets(10, 10, 10, 10));

		HBox spacer = new HBox(10);
		spacer.setMinHeight(40);
		Region spacer3 = new Region();
		VBox.setVgrow(spacer3, Priority.ALWAYS);
		VBox highScoreScreenBox = new VBox(10, TITLE_SCREEN_BOX, HIGH_SCORE_LABEL_BOX, HIGH_SCORES_INFO_LIST, spacer3, LOGOUT_BACK_BOX);
		highScoreScreenBox.setAlignment(Pos.TOP_CENTER);
		highScoreScreenBox.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

		BorderPane highScoreScreen = new BorderPane(highScoreScreenBox);
		highScoreScreen.setPadding(new Insets(10, 10, 10, 10));
		highScoreScreen.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));

		return highScoreScreen;
	}

	public Pane drawGameScreen() {
		message.setPromptText("message:");
		message.setMinWidth(290);

		sendButton.setMinWidth(50);

		chatLogs.setMinWidth(350);
		chatLogs.setMaxHeight(400);

		HBox CHAT_SEND_BOX = new HBox(10, message, sendButton);
		VBox CHAT_BOX = new VBox(10, chatLogs, CHAT_SEND_BOX);
		CHAT_BOX.setStyle("-fx-background-color: lightgray; -fx-padding: 10;");

		Text P1_LABEL = new Text("Player 1: " + user.userInfo.username);
		P1_LABEL.setFont(new Font("Serif", 13));
		P1_LABEL.setTextAlignment(TextAlignment.LEFT);
		P1_LABEL.setFill(Color.RED);
		Region spacer3 = new Region();
		HBox.setHgrow(spacer3, Priority.ALWAYS);
		Text P2_LABEL = new Text("Player 2: " + user.getOpponent());
		P2_LABEL.setFont(new Font("Serif", 13));
		P2_LABEL.setTextAlignment(TextAlignment.RIGHT);
		P2_LABEL.setFill(Color.YELLOW);
		HBox VERSUS_BOX = new HBox(10, P1_LABEL, spacer3, P2_LABEL);

		VBox BOARD_BOX = new VBox(10, VERSUS_BOX);
		BOARD_BOX.setAlignment(Pos.CENTER);
		BOARD_BOX.setBackground(new Background(new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, Insets.EMPTY)));
		BOARD_BOX.setPadding(new Insets(10, 10, 10, 10));

		for(int i = 0; i < tempBoard.length; i++) {
			HBox BOARD_ROW = new HBox(10);
			BOARD_ROW.setAlignment(Pos.CENTER);
			for(int j = 0; j < tempBoard[0].length; j++) {
				int value = tempBoard[i][j];

				Circle circle = new Circle(20);
				circle.setStroke(Color.BLACK);

				if(value == 0) {
					circle.setFill(Color.LIGHTGRAY);
				}
				else if(value == 1) {
					circle.setFill(Color.RED);
				}
				else {
					circle.setFill(Color.YELLOW);
				}

				BOARD_ROW.getChildren().add(circle);
			}
			BOARD_BOX.getChildren().add(BOARD_ROW);
		}

		if(gameStatus.equals("Continue")){
			HBox MOVES_BOX = new HBox(10, column1Button, column2Button, column3Button, column4Button, column5Button, column6Button, column7Button);
			MOVES_BOX.setAlignment(Pos.CENTER);
			column1Button.setMaxWidth(40);
			column2Button.setMaxWidth(40);
			column3Button.setMaxWidth(40);
			column4Button.setMaxWidth(40);
			column5Button.setMaxWidth(40);
			column6Button.setMaxWidth(40);
			column7Button.setMaxWidth(40);

			HBox SET_MOVES_BOX = new HBox(10, sendMoveButton, clearMoveButton);
			SET_MOVES_BOX.setAlignment(Pos.CENTER);
			BOARD_BOX.getChildren().addAll(MOVES_BOX, SET_MOVES_BOX);
			if(user.getOpponent().isEmpty()){
				sendMoveButton.setDisable(true);
				clearMoveButton.setDisable(true);
			}else {
				clearMoveButton.setDisable(false);
			}
		}


		HBox GAME_BOX = new HBox(10, CHAT_BOX, BOARD_BOX);
		GAME_BOX.setAlignment(Pos.CENTER);

		if(finalizedMove && user.loginCheck){
			setMoveButtonVisibility(tempBoard);
		}

		quitButton.setPrefWidth(100);
		quitButton.setPrefHeight(25);
		friendRequestButton.setPrefWidth(100);
		friendRequestButton.setPrefHeight(25);
		Region spacer2 = new Region();
		HBox.setHgrow(spacer2, Priority.ALWAYS);
		HBox QUIT_REQUEST_BOX = new HBox(10, quitButton, spacer2, friendRequestButton);
		QUIT_REQUEST_BOX.setAlignment(Pos.CENTER);
		QUIT_REQUEST_BOX.setPadding(new Insets(10, 10, 10, 10));
		if(user.userInfo.friends.contains(user.getOpponent()) || user.getOpponent().isEmpty()) {
			friendRequestButton.setDisable(true);
			quitButton.setDisable(true);
		} else {
			friendRequestButton.setDisable(false);
			quitButton.setDisable(false);
		}
		if(!user.getOpponent().isEmpty() || gameFinished){
			quitButton.setDisable(false);
		}

		VBox gameScreenBox = new VBox(10, TITLE_SCREEN_BOX, GAME_BOX, QUIT_REQUEST_BOX);
		gameScreenBox.setAlignment(Pos.TOP_CENTER);
		gameScreenBox.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

		BorderPane gameScreen = new BorderPane(gameScreenBox);
		gameScreen.setPadding(new Insets(10, 10, 10, 10));
		gameScreen.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));

//		printBoard();
		return gameScreenBox;
	}

	private void setMoveButtonVisibility(int[][] board){
		if(board[0][0] != 0){
			column1Button.setDisable(true);
		}
		else {
			column1Button.setDisable(false);
		}
		if(board[0][1] != 0){
			column2Button.setDisable(true);
		}
		else {
			column2Button.setDisable(false);
		}
		if(board[0][2] != 0){
			column3Button.setDisable(true);
		}
		else {
			column3Button.setDisable(false);
		}
		if(board[0][3] != 0){
			column4Button.setDisable(true);
		}
		else {
			column4Button.setDisable(false);
		}
		if(board[0][4] != 0){
			column5Button.setDisable(true);
		}
		else {
			column5Button.setDisable(false);
		}
		if(board[0][5] != 0){
			column6Button.setDisable(true);
		}
		else {
			column6Button.setDisable(false);
		}
		if(board[0][6] != 0){
			column7Button.setDisable(true);
		}
		else {
			column7Button.setDisable(false);
		}
	}

	private void printBoard(){
		System.out.println("Board");
		for(int i = 0; i < tempBoard.length; i++){
			for(int j = 0; j < tempBoard[i].length; j++){
				System.out.print(tempBoard[i][j] + " ");
			}
			System.out.println();
		}
		System.out.println();
		for(int i = 0; i < user.getBoard().length; i++){
			for (int j = 0; j < user.getBoard()[i].length; j++) {
				System.out.print(user.getBoard()[i][j] + " ");
			}
			System.out.println();
		}
	}

}
