package com.example.aplicatie;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Objects;

public class ChatApplication extends javafx.application.Application {


    protected static ArrayList<ClientFX> users = new ArrayList<>();
    protected static ArrayList<ChatRoom> chatRooms = new ArrayList<>();

    private double xOffset = 0;
    private double yOffset = 0;

    private void setChatRooms() throws SQLException, IOException {
        DataBaseConnection connection = new DataBaseConnection();
        Statement statement = connection.getConnection().createStatement();
        String query = "SELECT * FROM serverDetail";
        ResultSet resultSet = statement.executeQuery(query);
        while(resultSet.next()) {
            chatRooms.add(new ChatRoom(resultSet.getInt("AccountID")
                    ,resultSet.getInt("SecondAccountID")
                    ,resultSet.getInt("ServerPort")));
        }
        connection.getConnection().close();
    }

    private void setUsers() throws SQLException {
        DataBaseConnection connection = new DataBaseConnection();
        Statement statement = connection.getConnection().createStatement();
        String query = "SELECT * FROM accounts";
        ResultSet resultSet = statement.executeQuery(query);
        int i=0;
        while(resultSet.next()) {
            users.add(new ClientFX(resultSet.getString("Username")
                    ,resultSet.getString("Pass")));
            users.get(i).setId(resultSet.getInt("AccountID"));
            i++;
        }
        connection.getConnection().close();
    }

    public static int getIndexOfUser(int id) {
        for(int i =0;i<users.size();i++) {
            if(users.get(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }

    public static int getIndexOfUser(String username) {
        for(int i =0;i<users.size();i++) {
            if(Objects.equals(users.get(i).getUsername(), username)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void start(Stage stage) throws IOException, SQLException {
        setChatRooms();
        setUsers();
        FXMLLoader fxmlLoader = new FXMLLoader(ChatApplication.class.getResource("LoginPage.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 1000, 500);
        stage.setTitle("Login");
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.show();
    }

    public static int getPort(int accountID, int secondAccountID) {
        for(ChatRoom i : chatRooms) {
            if(i.getAccountID() == accountID && i.getSecondAccountID() == secondAccountID
                || i.getAccountID() == secondAccountID && i.getSecondAccountID() == accountID) {
                return i.getPort();
            }
        }
        return -1;
    }


    public static void main(String[] args) {
        launch();
    }
}