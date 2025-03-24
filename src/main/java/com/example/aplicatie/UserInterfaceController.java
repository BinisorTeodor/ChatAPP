package com.example.aplicatie;

//import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
//import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
//import de.jensd.fx.glyphs.weathericons.WeatherIcon;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.*;
import java.util.Objects;
import java.util.ResourceBundle;

public class UserInterfaceController implements Initializable {

    private ServerFX server;
    private static int id;
    private static ClientFX user;
    private static int port;
    private boolean isVisible = true;

    @FXML
    private ImageView profilePhoto, exitImage, messagesImage, groupImage;
    @FXML
    private Label username;
    @FXML
    private Button addFriend;
    @FXML
    private Button sendRequestButton;
    @FXML
    private TextField addFriendText;
    @FXML
    private ListView<String> listOfFriends;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private TableView<Account> requestTable;
    @FXML
    private TableColumn<Account,String> requestName;
    @FXML
    private TableColumn<Account,String> stopColumn;
    @FXML
    private TableView<Account> pendingsTable;
    @FXML
    private TableColumn<Account,String> pendingName;
    @FXML
    private TableColumn<Account,String> accept;
    @FXML
    private TableColumn<Account,String> decline;
    @FXML
    private VBox friendsVbox;
    private static double xOffset = 0;
    private static double yOffset = 0;

    ObservableList<Account> requestList = FXCollections.observableArrayList();
    ObservableList<Account> pendingsList = FXCollections.observableArrayList();



    protected  static void changeScene (MouseEvent event, String fxmlFile, String title) {

        try {
            Parent root;
            FXMLLoader fxmlLoader = new FXMLLoader(LoginController.class.getResource(fxmlFile));
            root = fxmlLoader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle(title);
            root.setOnMousePressed(event1 -> {
                xOffset = event1.getSceneX();
                yOffset = event1.getSceneY();
            });

            root.setOnMouseDragged(event1 -> {
                stage.setX(event1.getScreenX() - xOffset);
                stage.setY(event1.getScreenY() - yOffset);
            });
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private  void setFriendList() {

        try {
            DataBaseConnection connection = new DataBaseConnection();
            Statement statement = connection.getConnection().createStatement();
            boolean found = false;
            String query = ("SELECT FriendUsername" +
                    " FROM friends f " +
                    "INNER JOIN accounts a ON f.AccountID = a.AccountID " +
                    "WHERE f.AccountID = %d")
                    .formatted(id);
            ResultSet resultSet = statement.executeQuery(query);
            found = (resultSet !=null && resultSet.next());
            if(found) {
                try {
                    String list = resultSet.getString("FriendUsername");
                    addOnFriendList(list);
                    user.addFriend(list);
                    while(resultSet.next()) {
                        list = resultSet.getString("FriendUsername");
                        addOnFriendList(list);
                        user.addFriend(list);
                    }
                } catch (SQLException e) {
                    System.out.println("Problema la result set");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static int getPort() {
        return UserInterfaceController.port;
    }

    public static void setPort(int port) {
        UserInterfaceController.port =port;
    }

    public static void setId(int id) {
        UserInterfaceController.id = id;
    }

    public static void setUser(ClientFX users) {
        user = users;
    }

    public void addOnFriendList(String username) {
        listOfFriends.getItems().add(username);
    }

    private void resetPendingsTable() {
        pendingsList.clear();
        pendingName.setCellValueFactory(new PropertyValueFactory<Account,String>("name"));
        try {
            DataBaseConnection connection = new DataBaseConnection();
            Statement statement = connection.getConnection().createStatement();
            String query = "SELECT RequestedFrom FROM pendings WHERE AccountID = %d".formatted(id);
            ResultSet resultSet = statement.executeQuery(query);
            while(resultSet.next()) {
                pendingsList.add(new Account(resultSet.getString("RequestedFrom")));
                System.out.println(resultSet.getString("RequestedFrom"));
            }
            pendingsTable.setItems(pendingsList);
            connection.getConnection().close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void setPendingsTable() {

        pendingName.setCellValueFactory(new PropertyValueFactory<Account,String>("name"));
        try {
            DataBaseConnection connection = new DataBaseConnection();
            Statement statement = connection.getConnection().createStatement();
            String query = "SELECT RequestedFrom FROM pendings WHERE AccountID = %d".formatted(id);
            ResultSet resultSet = statement.executeQuery(query);
            while(resultSet.next()) {
                pendingsList.add(new Account(resultSet.getString("RequestedFrom")));
                System.out.println(resultSet.getString("RequestedFrom"));
            }
            //creating the accept cell
            Callback<TableColumn<Account,String>, TableCell<Account,String>> cellAccept = (TableColumn<Account,String> param) -> new TableCell<Account, String>() {
                @Override
                protected void updateItem(String s, boolean b) {
                    super.updateItem(s, b);
                    if(b) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        Button acceptButton = new Button();
                        Image img = new Image("file:\\C:\\Users\\teodo\\IdeaProjects\\LaboratorMostenire\\Aplicatie\\src\\main\\resources\\com\\example\\aplicatie\\icons\\accept.png");
                        ImageView imageView = new ImageView(img);
                        imageView.setFitHeight(20);
                        imageView.setFitWidth(20);
                        acceptButton.setStyle("-fx-background-color: transparent;");
                        acceptButton.setGraphic(imageView);
                        acceptButton.setOnMouseClicked((MouseEvent event) -> {
                            String nameToDelete = pendingsList.get(getTableRow().getIndex()).getName();
                            String check = "SELECT RequestedFrom FROM pendings WHERE AccountID = %d and RequestedFrom = '%s'".formatted(id,nameToDelete);
                            try {
                                ResultSet checkRs = statement.executeQuery(check);
                                if(checkRs.next()) {
                                    boolean addWithSuccess = user.addFriend(pendingsList.get(getTableRow().getIndex()).getName());
                                    if(addWithSuccess) {
                                        try {


                                            String selectQuery = "SELECT AccountID FROM accounts WHERE Username = '%s'".formatted(nameToDelete);
                                            ResultSet rs = statement.executeQuery(selectQuery);
                                            if (rs.next()) {
                                                int idToDelete = rs.getInt("AccountID");
                                                String deleteQueryFromRequests = ("DELETE FROM friendRequests " +
                                                        "WHERE RequestedTo = '%s' and AccountID = %d").formatted(user.getUsername(), idToDelete);
                                                String deleteQueryFromPendings = ("DELETE FROM pendings " +
                                                        "WHERE RequestedFrom = '%s' and AccountID = %d").formatted(nameToDelete, id);
                                                PreparedStatement preparedStatement = connection.getConnection().prepareStatement(deleteQueryFromRequests);
                                                preparedStatement.execute();
                                                preparedStatement = connection.getConnection().prepareStatement(deleteQueryFromPendings);
                                                preparedStatement.execute();

                                                addOnFriendList(pendingsList.get(getTableRow().getIndex()).getName());
                                                pendingsTable.getItems().remove(getTableRow().getIndex());
                                            }
                                        } catch (Exception e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                } else {
                                    String deleteQueryFromPendings = ("DELETE FROM friendRequests " +
                                            "WHERE RequestedTo = '%s' and AccountID = %d").formatted(nameToDelete, id);
                                    PreparedStatement preparedStatement = connection.getConnection().prepareStatement(deleteQueryFromPendings);
                                    preparedStatement.execute();
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Error");
                                    alert.setContentText("Sorry you moved to slow, the person who sent the requested form deleted it, moments ago...");
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        });
                        HBox manageButton = new HBox(acceptButton);
                        manageButton.setStyle("fx-alignment:center");
                        HBox.setMargin(acceptButton, new Insets(2,2,0,3));
                        setGraphic(manageButton);
                        setText(null);
                    }
                }
            };
            accept.setCellFactory(cellAccept);
            //creating de decline button cell
            Callback<TableColumn<Account,String>, TableCell<Account,String>> cellDecline = (TableColumn<Account,String> param) -> new TableCell<Account, String>() {
                @Override
                protected void updateItem(String s, boolean b) {
                    super.updateItem(s, b);
                    if(b) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        Button declineButton = new Button();
                        Image img = new Image("file:\\C:\\Users\\teodo\\IdeaProjects\\LaboratorMostenire\\Aplicatie\\src\\main\\resources\\com\\example\\aplicatie\\icons\\button.png");
                        ImageView imageView = new ImageView(img);
                        imageView.setFitHeight(20);
                        imageView.setFitWidth(20);
                        declineButton.setGraphic(imageView);
                        declineButton.setStyle("-fx-background-color: transparent;");
                        declineButton.setOnMouseClicked((MouseEvent event) -> {
                            try {
                                String nameToDelete = pendingsList.get(getTableRow().getIndex()).getName();

                                String selectQuery = "SELECT AccountID FROM accounts WHERE Username = '%s'".formatted(nameToDelete);
                                ResultSet rs = statement.executeQuery(selectQuery);
                                if(rs.next()) {
                                    int idToDelete = rs.getInt("AccountID");
                                    System.out.println(idToDelete);
                                    String deleteQueryFromRequests = ("DELETE FROM friendRequests " +
                                            "WHERE RequestedTo = '%s' and AccountID = '%s'").formatted(user.getUsername(), idToDelete);

                                    String deleteQueryFromPendings = ("DELETE FROM pendings" +
                                            " WHERE AccountID = %d and RequestedFrom = '%s'").formatted(id,nameToDelete);

                                    PreparedStatement preparedStatement = connection.getConnection().prepareStatement(deleteQueryFromRequests);
                                    preparedStatement.execute();

                                    preparedStatement = connection.getConnection().prepareStatement(deleteQueryFromPendings);
                                    preparedStatement.execute();
                                    pendingsTable.getItems().remove(getTableRow().getIndex());
                                }

                            } catch (SQLException e) {
                                System.out.println("Error deleting from the database");
                                throw new RuntimeException(e);
                            }

                        });
                        HBox manageButton = new HBox(declineButton);
                        manageButton.setStyle("fx-alignment:center");
                        HBox.setMargin(declineButton, new Insets(2,3,0,2));
                        setGraphic(manageButton);
                        setText(null);
                    }
                }
            };
            decline.setCellFactory(cellDecline);
            pendingsTable.setItems(pendingsList);
            connection.getConnection().close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    private void resetRequestTable() {
        requestList.clear();
        requestName.setCellValueFactory(new PropertyValueFactory<Account,String>("name"));
        try {
            DataBaseConnection connection = new DataBaseConnection();
            Statement statement = connection.getConnection().createStatement();
            String query = "SELECT RequestedTo FROM friendRequests WHERE AccountID = %d".formatted(id);
            ResultSet resultSet = statement.executeQuery(query);
            while(resultSet.next()) {
                requestList.add(new Account(resultSet.getString("RequestedTo")));
                System.out.println(resultSet.getString("RequestedTo"));
            }
            requestTable.setItems(requestList);
            connection.getConnection().close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private void setRequestTable() {
        requestName.setCellValueFactory(new PropertyValueFactory<Account,String>("name"));
        try {
            DataBaseConnection connection = new DataBaseConnection();
            Statement statement = connection.getConnection().createStatement();
            String query = "SELECT RequestedTo FROM friendRequests WHERE AccountID = %d".formatted(id);
            ResultSet resultSet = statement.executeQuery(query);
            while(resultSet.next()) {
                requestList.add(new Account(resultSet.getString("RequestedTo")));
                System.out.println(resultSet.getString("RequestedTo"));
            }
            //creating the accept cell
            Callback<TableColumn<Account,String>, TableCell<Account,String>> cellAccept = (TableColumn<Account,String> param) -> new TableCell<Account, String>() {
                @Override
                protected void updateItem(String s, boolean b) {
                    super.updateItem(s, b);
                    if(b) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        Button stopButton = new Button();
                        Image img = new Image("file:\\C:\\Users\\teodo\\IdeaProjects\\LaboratorMostenire\\Aplicatie\\src\\main\\resources\\com\\example\\aplicatie\\icons\\button.png");
                        ImageView imageView = new ImageView(img);
                        imageView.setFitHeight(20);
                        imageView.setFitWidth(20);
                        stopButton.setGraphic(imageView);
                        stopButton.setStyle("-fx-background-color: transparent;");
                        stopButton.setOnMouseClicked((MouseEvent event) -> {
                            try {
                                String nameToDelete = requestList.get(getTableRow().getIndex()).getName();

                                String selectQuery = "SELECT AccountID FROM accounts WHERE Username = '%s'".formatted(nameToDelete);
                                String checkQuery = "SELECT RequestedTo FROM friendRequests WHERE AccountID = %d and RequestedTo = '%s'".formatted(id, nameToDelete);
                                Statement checkStatement = connection.getConnection().createStatement();
                                ResultSet checkRs = checkStatement.executeQuery(checkQuery);
                                ResultSet rs = statement.executeQuery(selectQuery);
                                if(checkRs != null && checkRs.next()) {
                                    if(rs.next()) {
                                        int idToDelete = rs.getInt("AccountID");
                                        System.out.println(idToDelete);
                                        String deleteQueryFromRequests = ("DELETE FROM friendRequests " +
                                                "WHERE RequestedTo = '%s' and AccountID = %d").formatted(nameToDelete, id);

                                        String deleteQueryFromPendings = ("DELETE FROM pendings" +
                                                " WHERE AccountID = %d and RequestedFrom = '%s'").formatted(idToDelete,user.getUsername());

                                        PreparedStatement preparedStatement = connection.getConnection().prepareStatement(deleteQueryFromRequests);
                                        preparedStatement.execute();

                                        preparedStatement = connection.getConnection().prepareStatement(deleteQueryFromPendings);
                                        preparedStatement.execute();
                                        requestTable.getItems().remove(getTableRow().getIndex());
                                    }
                                } else {
                                    String deleteQueryFromRequests = ("DELETE FROM friendRequests " +
                                            "WHERE RequestedTo = '%s' and AccountID = %d").formatted(nameToDelete, id);
                                    PreparedStatement preparedStatement = connection.getConnection().prepareStatement(deleteQueryFromRequests);
                                    preparedStatement.execute();
                                    addOnFriendList(nameToDelete);
                                    requestTable.getItems().remove(getTableRow().getIndex());
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Error");
                                    alert.setContentText("Sorry, the person already accepted this request");
                                    alert.showAndWait();
                                }


                            } catch (SQLException e) {
                                System.out.println("Error deleting from the database");
                                throw new RuntimeException(e);
                            }

                        });
                        HBox manageButton = new HBox(stopButton);
                        manageButton.setStyle("fx-alignment:center");
                        HBox.setMargin(stopButton, new Insets(2,2,0,3));
                        setGraphic(manageButton);
                        setText(null);
                    }
                }
            };
            stopColumn.setCellFactory(cellAccept);
            requestTable.setItems(requestList);
            connection.getConnection().close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    private static boolean serverIsOpen(Statement statement, String nameOfFriend) {
        int portFriend=0;
        try {
            String selectQuery = "SELECT AccountID FROM accounts WHERE Username = '%s'".formatted(nameOfFriend);
            ResultSet rs = statement.executeQuery(selectQuery);
            if (rs.next()) {
                int idFriend = rs.getInt("AccountID");

                portFriend = ChatApplication.getPort(id, idFriend);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String query = "SELECT isOpen FROM serverDetail WHERE ServerPort = %d".formatted(portFriend);
        boolean found;
        try {
            ResultSet resultSet = statement.executeQuery(query);
            found = (resultSet !=null && resultSet.next());
            if(found && resultSet.getInt("isOpen") == 0) {
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return true;

    }

    @FXML
    public void onAddFriendButton() {
        listOfFriends.getSelectionModel().clearSelection();
        addFriend.setOpacity(0);
        addFriend.setDisable(true);
        sendRequestButton.setOpacity(1);
        sendRequestButton.setDisable(false);
        addFriendText.setOpacity(1);
        addFriendText.setDisable(false);
    }

    @FXML
    public void onSendRequestButton() {
        try {
            listOfFriends.getSelectionModel().clearSelection();
            sendRequestButton.setOpacity(1);
            sendRequestButton.setDisable(false);
            DataBaseConnection connection = new DataBaseConnection();
            Statement statement = connection.getConnection().createStatement();


            boolean foundAccount = false;
            boolean foundFriend = false;
            boolean foundRequest = false;

            String selectExistRequest = "SELECT RequestedTo FROM friendRequests WHERE RequestedTo = '%s' and AccountID = %d"
                    .formatted(addFriendText.getText(),id);
            try {
                ResultSet resultSet = statement.executeQuery(selectExistRequest);
                foundRequest = (resultSet !=null && resultSet.next());
            } catch (Exception e) {
                System.out.println("Ceva nu-i bun la query");
            }

            String selectExistAccount = "SELECT Username FROM accounts WHERE Username = '%s'"
                    .formatted(addFriendText.getText());
            try {
                ResultSet resultSet = statement.executeQuery(selectExistAccount);
                foundAccount = (resultSet !=null && resultSet.next());
            } catch (Exception e) {
                System.out.println("Ceva nu-i bun la query");
            }

            String selectFriendQuery = "SELECT FriendUsername FROM friends WHERE FriendUsername = '%s' and AccountID = %d"
                    .formatted(addFriendText.getText(),id);
            try {
                ResultSet resultSet = statement.executeQuery(selectFriendQuery);
                foundFriend = (resultSet !=null && resultSet.next());
            } catch (Exception e) {
                System.out.println("Ceva nu-i bun la query");
            }

            if(!foundAccount) {
                System.out.println("The username doesn't exist");
                return;
            }

            if(foundFriend) {
                System.out.println("He is already your friend");
                return;
            }

            if(Objects.equals(addFriendText.getText(), user.getUsername())) {
                System.out.println("You cannot add yourself to the friend list");
                return;
            }

            if(foundRequest) {
                System.out.println("You already requested this person, please be patient");
                return;
            }

            String query = "INSERT INTO friendRequests (AccountID, RequestedTo) VALUES (?, ?)";
            PreparedStatement psInsert = statement.getConnection().prepareStatement(query);
            psInsert.setInt(1,id);
            psInsert.setString(2,addFriendText.getText());
            psInsert.executeUpdate();
            boolean found;
            String selectQuery = "SELECT AccountID FROM accounts WHERE Username = '%s'"
                    .formatted(addFriendText.getText());
            try {
                ResultSet resultSet = statement.executeQuery(selectQuery);
                found = (resultSet !=null && resultSet.next());
                if(found) {
                    query = "INSERT INTO pendings (AccountID, RequestedFrom) VALUES (?, ?)";
                    psInsert = statement.getConnection().prepareStatement(query);
                    psInsert.setInt(1,resultSet.getInt("AccountID"));
                    psInsert.setString(2,user.getUsername());
                    psInsert.executeUpdate();
                }
            } catch (Exception e) {
                System.out.println("Ceva nu-i bun la query");
            }

//            sendRequestButton.setOpacity(0);
//            sendRequestButton.setDisable(true);
//            addFriendText.setOpacity(0);
//            addFriendText.setDisable(true);
            addFriendText.clear();
//            addFriend.setOpacity(1);
//            addFriend.setDisable(false);
            System.out.println("The request has been send with success");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        requestName.setCellValueFactory(new PropertyValueFactory<Account,String>("name"));
        resetRequestTable();
    }

    @FXML
    public void onExitImage() {
        System.exit(0);
    }

    @FXML
    public void onAddFriendImage() {
//        TranslateTransition slide = new TranslateTransition();
//        slide.setDuration(Duration.seconds(1));
//        slide.setNode(friendsVbox);
//        slide.setToY(-550);
//        slide.play();
//        //friendsVbox.setOpacity(1);
//        //friendsVbox.setTranslateX(444);
//        slide.setOnFinished(event -> {
//            Stage stage =(Stage) sendRequestButton.getScene().getWindow();
//            friendsVbox.setOpacity(0);
//            stage.setWidth(444);
//        });
        groupImage.setDisable(true);
        TranslateTransition slide = new TranslateTransition(Duration.seconds(0.5), friendsVbox);
        FadeTransition fade = new FadeTransition(Duration.seconds(0.5), friendsVbox);
        SequentialTransition sequence = new SequentialTransition(fade, slide);
        if (isVisible) {
            // Slide out and fade out
            slide.setToX(-250);
            fade.setToValue(0);
            Stage stage =(Stage) sendRequestButton.getScene().getWindow();
            stage.setWidth(444);
            sequence.play();
            slide.setOnFinished(event -> {
                //Stage stage =(Stage) sendRequestButton.getScene().getWindow();
                friendsVbox.setOpacity(0);
                groupImage.setDisable(false);
                //stage.setWidth(444);
            });
            isVisible = false;
        } else {
            // Slide in and fade in
            slide.setToX(0);
            fade.setToValue(1);
            Stage stage =(Stage) sendRequestButton.getScene().getWindow();
            stage.setWidth(945);
            sequence.play();
            slide.setOnFinished(event -> {
                friendsVbox.setOpacity(1);
                groupImage.setDisable(false);
            });
            isVisible = true;
        }

    }

    @FXML
    public void onProfileImage() {
        FileChooser fileChooser = new FileChooser();

        // Set file filters for images
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
        );

        // Show open dialog
        File file = fileChooser.showOpenDialog(profilePhoto.getScene().getWindow());
        if (file != null) {
            // Load and display the selected image
            Image image = new Image(file.toURI().toString());
            try {
                DataBaseConnection connection = new DataBaseConnection();
                Statement statement = connection.getConnection().createStatement();
                String update = "UPDATE accounts SET profilePhoto = '%s' WHERE AccountID = %d".formatted(file.toURI().toString(), id);
                statement.executeUpdate(update);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            profilePhoto.setImage(image);
        }
    }

    //disable clicked items on listview by clicking anywhere on screen
    @FXML
    public void onScreenAction() {
        listOfFriends.getSelectionModel().clearSelection();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //setting up the environment for the start of the application

        setRequestTable();
        setPendingsTable();
        requestTable.setPlaceholder(new Label(""));
        pendingsTable.setPlaceholder(new Label(""));
        user.setId(id);
        username.setText(user.getUsername());
        user.setProfileImageURI(user.getProfileImageURI());
        if(user.getProfileImageURI() != null) {
            profilePhoto.setImage(new Image(user.getProfileImageURI()));
        }
        System.out.println(id);
        listOfFriends.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                System.out.println(listOfFriends.getSelectionModel().getSelectedItem());
                listOfFriends.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        try {
                            String nameOfFriend = listOfFriends.getSelectionModel().getSelectedItem();
                            DataBaseConnection connection = new DataBaseConnection();
                            Statement statement = connection.getConnection().createStatement();
                            String selectQuery = "SELECT AccountID FROM accounts WHERE Username = '%s'".formatted(nameOfFriend);
                            ResultSet rs = statement.executeQuery(selectQuery);
                            int idFriend = -1;
                            if (rs.next()) {
                                idFriend = rs.getInt("AccountID");
                            }

                            int port = ChatApplication.getPort(id,idFriend);
                            if(!serverIsOpen(statement,nameOfFriend)) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            ServerFX.startGUI(port);
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                }).start();
                                String update = ("UPDATE serverDetail " +
                                        "SET isOpen = 1 " +
                                        "WHERE (AccountID = %d and SecondAccountID =%d)").formatted(id,idFriend) +
                                        "OR (AccountID =%d and SecondAccountID = %d)".formatted(idFriend,id);
                                statement.executeUpdate(update);
                            }

                            Socket socket = new Socket("localhost",port);
                            user.setTimestamp(port,new Timestamp(System.currentTimeMillis()));
                            ChatToFriendController.setClient(user);
                            ChatToFriendController.setFriend(nameOfFriend);
                            ChatToFriendController.setClientForChat(socket);
                            changeScene(mouseEvent, "ChatToFriend.fxml", "Chat");

                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }


                    }
                });
            }
        });
        //Setting up the friend list every login from the database
        setFriendList();
    }
}
