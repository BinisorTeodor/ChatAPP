package com.example.aplicatie;


import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import javafx.event.ActionEvent;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Scanner;

public class ChatToFriendController implements Initializable {


    private static ClientFX user;
    private static ClientFX friend;
    @FXML
    private Button sendButton;
    @FXML
    private TextField textToSend;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Label friendName;
    @FXML
    private VBox vboxMessages;
    @FXML
    private Button back;
    @FXML
    private ImageView profilePhoto, deleteFriend;

    public static void setFriend(String username) {
        ChatToFriendController.friend = ChatApplication.users.get(ChatApplication.getIndexOfUser(username));
    }

    public ChatToFriendController() {
        listenForMessage();
    }

    public static void setClient(ClientFX user) {
        ChatToFriendController.user = user;
    }


    public static void setClientForChat(Socket socket) {
        try {
            ChatToFriendController.user.setSocket(socket);
            user.setBufferedReader(new BufferedReader(new InputStreamReader(socket.getInputStream())));
            user.setBufferedWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
            //listenForMessage();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void messageToSend(String message) {
        try {
            user.getBufferedWriter().write(message);
            user.getBufferedWriter().newLine();
            user.getBufferedWriter().flush();
        } catch (IOException e) {
            closeEveryThing(user.getSocket(), user.getBufferedWriter(), user.getBufferedReader());
        }
    }

    private void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (user.getSocket().isConnected()) {
                    try {
                        String messageToReceive = user.getBufferedReader().readLine();
                        addLabel(messageToReceive, vboxMessages);
                    } catch (IOException e) {
                        closeEveryThing(user.getSocket(), user.getBufferedWriter(), user.getBufferedReader());
                    }
                }
            }
        }).start();
    }

    private void closeEveryThing(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader) {
        try {
            if(socket != null) {
                socket.close();
            }
            if(bufferedReader != null) {
                bufferedReader.close();
            }
            if(bufferedWriter != null) {
                bufferedWriter.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setVboxMessages(VBox vboxMessages) {
        LinkedList<Message> messages = user.getMessage(friend.getId(), user.getId(),
                ChatApplication.getPort(user.getId(),friend.getId()));
        for (Message m : messages) {
            if(m.getId()==user.getId()){
                System.out.println(m.getContent());
                HBox hbox = new HBox();
                hbox.setSpacing(10);
                hbox.setAlignment(Pos.CENTER_RIGHT);
                hbox.setPadding(new Insets(5, 5, 5, 10));
                Text content = new Text(m.getContent());
                Text name = new Text(": You");

                TextFlow nameFlow = new TextFlow(name);
                nameFlow.setStyle("-fx-color: rgb(100, 100, 100); " +
                        "-fx-background-color: rgb(80, 201, 132); " +
                        "-fx-background-radius: 20px;");
                nameFlow.setPadding(new Insets(5, 10, 5, 10));
                name.setFill(Color.color(0.350, 0.350, 0.350));


                TextFlow textFlow = new TextFlow(content);

                textFlow.setStyle("-fx-color: rgb(100, 100, 100); " +
                        "-fx-background-color: rgb(80, 201, 132); " +
                        "-fx-background-radius: 20px;");
                textFlow.setPadding(new Insets(5, 10, 5, 10));
                content.setFill(Color.color(0.350, 0.350, 0.350));


                hbox.getChildren().add(textFlow);
                hbox.getChildren().add(nameFlow);
                vboxMessages.getChildren().add(hbox);
            } else {
                System.out.println(m.getContent());
                //addLabel(m.getContent(),vboxMessages);
                HBox hbox = new HBox();
                hbox.setSpacing(10);
                hbox.setAlignment(Pos.CENTER_LEFT);
                hbox.setPadding(new Insets(5, 5, 5, 10));
                Text text = new Text(m.getContent());

                Text name = new Text( friend.getUsername()+ ": ");

                TextFlow nameFlow = new TextFlow(name);
                nameFlow.setStyle("-fx-color: rgb(100, 100, 100); " +
                        "-fx-background-color: rgb(255, 255, 255); " +
                        "-fx-background-radius: 20px;");
                nameFlow.setPadding(new Insets(5, 10, 5, 10));
                name.setFill(Color.color(0.350, 0.350, 0.350));


                TextFlow textFlow = new TextFlow(text);

                textFlow.setStyle("-fx-color: rgb(100, 100, 100); " +
                        "-fx-background-color: rgb(255, 255, 255); " +
                        "-fx-background-radius: 20px;");
                textFlow.setPadding(new Insets(5, 10, 5, 10));
                text.setFill(Color.color(0.350, 0.350, 0.350));

                hbox.getChildren().add(nameFlow);
                hbox.getChildren().add(textFlow);

                vboxMessages.getChildren().add(hbox);
            }
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        deleteFriend.setOnMouseClicked(mouseEvent -> {

        });
        friendName.setText(friend.getUsername());
        if(friend.getProfileImageURI() != null) {
            profilePhoto.setImage(new Image(friend.getProfileImageURI()));
        }

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        Image img = new Image("file:\\C:\\Users\\teodo\\IdeaProjects\\LaboratorMostenire\\Aplicatie\\src\\main\\resources\\com\\example\\aplicatie\\icons\\back.png");
        ImageView imageView = new ImageView(img);
        imageView.setFitHeight(30);
        imageView.setFitWidth(30);
        back.setGraphic(imageView);
        back.setStyle("-fx-background-color: transparent;");

        setVboxMessages(vboxMessages);

        Image sendImg = new Image("file:\\C:\\Users\\teodo\\IdeaProjects\\LaboratorMostenire\\Aplicatie\\src\\main\\resources\\com\\example\\aplicatie\\icons\\message.png");
        ImageView sendImgView = new ImageView(sendImg);
        sendImgView.setFitHeight(30);
        sendImgView.setFitWidth(30);
        sendButton.setGraphic(sendImgView);
        vboxMessages.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                scrollPane.setVvalue((Double) t1);
            }
        });

        sendButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String messageToSend = textToSend.getText();
                if(!messageToSend.isEmpty()) {
                    HBox hbox = new HBox();
                    hbox.setSpacing(10);
                    hbox.setAlignment(Pos.CENTER_RIGHT);
                    hbox.setPadding(new Insets(5, 5, 5, 10));


                    Text text = new Text(messageToSend);

                    Text name = new Text(": You");

                    TextFlow nameFlow = new TextFlow(name);
                    nameFlow.setStyle("-fx-color: rgb(100, 100, 100); " +
                            "-fx-background-color: rgb(80, 201, 132); " +
                            "-fx-background-radius: 20px;");
                    nameFlow.setPadding(new Insets(5, 10, 5, 10));
                    name.setFill(Color.color(0.350, 0.350, 0.350));

                    TextFlow textFlow = new TextFlow(text);

                    textFlow.setStyle("-fx-color: rgb(100, 100, 100); " +
                            "-fx-background-color: rgb(80, 201, 132); " +
                            "-fx-background-radius: 20px;");
                    textFlow.setPadding(new Insets(5, 10, 5, 10));
                    text.setFill(Color.color(0.350, 0.350, 0.350));

                    hbox.getChildren().add(textFlow);
                    hbox.getChildren().add(nameFlow);
                    vboxMessages.getChildren().add(hbox);

                    messageToSend(messageToSend);
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    user.setMessage(friend.getId(), user.getId(), messageToSend,
                            ChatApplication.getPort(user.getId(),friend.getId()),timestamp);

                    textToSend.clear();
                }
            }
        });

    }

    public static void addLabel(String messageFromFriend, VBox vbox) {
        HBox hbox = new HBox();
        hbox.setSpacing(10);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setPadding(new Insets(5, 5, 5, 10));

        Text text = new Text(messageFromFriend);

        Text name = new Text(friend.getUsername() + ": ");

        TextFlow nameFlow = new TextFlow(name);
        nameFlow.setStyle("-fx-color: rgb(100, 100, 100); " +
                "-fx-background-color: rgb(255, 255, 255); " +
                "-fx-background-radius: 20px;");
        nameFlow.setPadding(new Insets(5, 10, 5, 10));
        name.setFill(Color.color(0.350, 0.350, 0.350));

        TextFlow textFlow = new TextFlow(text);



        textFlow.setStyle("-fx-background-color: rgb(255, 255, 255); " +
                          "-fx-background-radius: 20px;" +
                          "-fx-color: rgb(100, 100, 100);");
        textFlow.setPadding(new Insets(5, 10, 5, 10));
        text.setFill(Color.color(0.350, 0.350, 0.350));

        hbox.getChildren().add(nameFlow);
        hbox.getChildren().add(textFlow);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                vbox.getChildren().add(hbox);
            }
        });


    }

    @FXML
    public void onBackButton(ActionEvent event) {
        try {
            DataBaseConnection connection = new DataBaseConnection();
            Statement statement = connection.getConnection().createStatement();
            user.setTimestamp(ChatApplication.getPort(user.getId(),friend.getId()), new Timestamp(1000));
            if(friend.getTimestamp(ChatApplication.getPort(user.getId(),friend.getId())) != null
                    && Objects.equals(friend.getTimestamp(ChatApplication.getPort(user.getId(),friend.getId())), new Timestamp(1000))
                    || friend.getTimestamp(ChatApplication.getPort(user.getId(),friend.getId())) == null) {
                String update = "UPDATE serverDetail SET isOpen = 0 WHERE ServerPort = %s"
                        .formatted(ChatApplication.getPort(user.getId(),friend.getId()));
                statement.executeUpdate(update);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        LoginController.changeScene(event,"UserInterface2.fxml", "ChatAPP");
    }

    @FXML
    public void onDeleteFriend(ActionEvent event) {
        System.out.println("salut");
        try {
            DataBaseConnection connection = new DataBaseConnection();
            Statement statement = connection.getConnection().createStatement();
            String deleteQuery = "DELETE FROM serverDetail WHERE ServerPort = %d".formatted(ChatApplication.getPort(user.getId(),friend.getId()));
            String deleteQueryFriend = "DELETE FROM friends WHERE AccountID = %d and FriendUsername = '%s'".formatted(user.getId(), friend.getUsername());
            String deleteQueryFriend2 = "DELETE FROM friends WHERE AccountID = %d and FriendUsername = '%s'".formatted(friend.getId(), user.getUsername());
            statement.executeUpdate(deleteQuery);
            Statement statementFriend = connection.getConnection().createStatement();
            statementFriend.executeUpdate(deleteQueryFriend);
            statementFriend.executeUpdate(deleteQueryFriend2);
            Alert alertType = new Alert(Alert.AlertType.INFORMATION);
            alertType.setContentText("You successfully deleted the friend");
            alertType.showAndWait();
            LoginController.changeScene(event,"UserInterface2.fxml", "ChatAPP");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

}
