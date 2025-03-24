package com.example.aplicatie;

import com.mysql.cj.jdbc.MysqlDataSource;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.*;

import static com.example.aplicatie.ChatApplication.users;

import com.example.aplicatie.UserInterfaceController.*;
import javafx.stage.StageStyle;

public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField usernameFieldRegister;
    @FXML
    private PasswordField passwordFieldRegister;
    private static double xOffset = 0;
    private static double yOffset = 0;

    @FXML
    private void onRegister(ActionEvent event) {

        changeScene(event, "Register.fxml", "Register");
    }

    @FXML
    private void onBack(ActionEvent event) {
        changeScene(event,"LoginPage.fxml", "Login");
    }

    private void insertAccount(ActionEvent event, Statement statement) throws SQLException {

        String query = "INSERT INTO accounts (Username, Pass) VALUES (?, ?)";

        if(!checkExistingAccount(statement,usernameFieldRegister.getText(),"accounts", "Username"))
        {
            if(!usernameFieldRegister.getText().isBlank() && !passwordFieldRegister.getText().isBlank()) {
                PreparedStatement psInsert = statement.getConnection().prepareStatement(query);
                psInsert.setString(1,usernameFieldRegister.getText());
                psInsert.setString(2,passwordFieldRegister.getText());
                psInsert.executeUpdate();
                Alert noAccount = new Alert(Alert.AlertType.INFORMATION);
                noAccount.setContentText("You have created an account with success");
                ClientFX user = new ClientFX(usernameFieldRegister.getText(), passwordFieldRegister.getText());
                users.add(user);
                noAccount.setTitle("Congrats");
                noAccount.showAndWait();
                changeScene(event,"LoginPage.fxml","Login");
            } else {
                Alert noFields = new Alert(Alert.AlertType.WARNING);
                noFields.setContentText("Please enter a valid username and password");
                noFields.showAndWait();
            }

        } else {
            Alert noAccount = new Alert(Alert.AlertType.ERROR);
            noAccount.setContentText("This account already exist");
            noAccount.setTitle("Oops");
            noAccount.showAndWait();
        }
    }

    public int getID(Statement statement) {
        String query = "SELECT AccountID FROM accounts WHERE Username = '%s'".formatted(usernameField.getText());
        boolean found;
        try {
            ResultSet resultSet = statement.executeQuery(query);
            found = (resultSet !=null && resultSet.next());
            if(found) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return -1;
    }


    public boolean checkExistingAccount(Statement statement, String username, String table, String column) {

        boolean found = false;
        String query = "SELECT * FROM %s WHERE %s = '%s'"
                .formatted(table,column, username);
        try {
            ResultSet resultSet = statement.executeQuery(query);
            found = (resultSet !=null && resultSet.next());
        } catch (Exception e) {
            System.out.println("Ceva nu-i bun la query");
        }
        return found;
    }



    protected static void changeScene (ActionEvent event, String fxmlFile, String title) {

        try {
            Parent root;
            FXMLLoader fxmlLoader = new FXMLLoader(LoginController.class.getResource(fxmlFile));
            root = fxmlLoader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            root.setOnMousePressed(event1 -> {
                xOffset = event1.getSceneX();
                yOffset = event1.getSceneY();
            });

            root.setOnMouseDragged(event1 -> {
                stage.setX(event1.getScreenX() - xOffset);
                stage.setY(event1.getScreenY() - yOffset);
            });
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @FXML
    protected void onLoginButton(ActionEvent event) {

        DataBaseConnection connection = new DataBaseConnection();
        try {
            Statement statement = connection.getConnection().createStatement();
            if(!usernameField.getText().isBlank() && !passwordField.getText().isBlank()) {
                if (!checkExistingAccount(statement, usernameField.getText(),"accounts", "Username")){
                    Alert noAccount = new Alert(Alert.AlertType.ERROR);
                    noAccount.setContentText("Your account was not found in the database");
                    noAccount.setTitle("Ooops");
                    noAccount.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("You have logged in with success");
                    alert.setTitle("Logged in");
                    //Setting the username for the interface
                    ClientFX user = new ClientFX(usernameField.getText(), passwordField.getText());

                    UserInterfaceController.setUser(user);
                    //Searching for the id of the user in the database, so can pass to the interface controller
                    int id = getID(statement);
                    if(id == -1) {
                        System.out.println("It has been an error setting up the id");
                    } else {
                        UserInterfaceController.setId(id);
                    }
                    alert.showAndWait();
                    changeScene(event, "UserInterface2.fxml", "ChatAPP");
                }
            } else {
                Alert noFields = new Alert(Alert.AlertType.WARNING);
                noFields.setContentText("Please enter a valid username and password");
                noFields.showAndWait();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (connection.getConnection() != null) {
                try {
                    connection.getConnection().close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @FXML
    protected void onRegisterButton(ActionEvent event) {
        DataBaseConnection connection = new DataBaseConnection();
        try {
            Statement statement = connection.getConnection().createStatement();
            insertAccount(event,statement);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if(connection.getConnection() != null) {
                try {
                    connection.getConnection().close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}