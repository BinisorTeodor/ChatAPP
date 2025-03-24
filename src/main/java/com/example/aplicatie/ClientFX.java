package com.example.aplicatie;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Scanner;

public class ClientFX {

    private int id;
    private String username;
    private String password;
    private String profileImageURI;
    private ArrayList<String> friends;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public String getProfileImageURI() {
        try {
            DataBaseConnection connection = new DataBaseConnection();
            Statement statement = connection.getConnection().createStatement();
            String selectQuery = "SELECT profilePhoto FROM accounts WHERE AccountID = %d".formatted(id);
            ResultSet resultSet = statement.executeQuery(selectQuery);
            while (resultSet.next()) {
                profileImageURI = resultSet.getString("profilePhoto");
            }
            return profileImageURI;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setProfileImageURI(String profileImageURI) {
        this.profileImageURI = profileImageURI;
    }

    public BufferedWriter getBufferedWriter() {
        return bufferedWriter;
    }

    public void setBufferedWriter(BufferedWriter bufferedWriter) {
        this.bufferedWriter = bufferedWriter;
    }

    public BufferedReader getBufferedReader() {
        return bufferedReader;
    }

    public void setBufferedReader(BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setTimestamp(int port,Timestamp timestamp) {
        try {
            DataBaseConnection connection= new DataBaseConnection();
            Statement statement = connection.getConnection().createStatement();


            String firstQuery = "SELECT AccountID FROM serverDetail WHERE AccountID = %d and ServerPort = %d".formatted(id,port);
            String secondQuery = "SELECT SecondAccountID FROM serverDetail WHERE SecondAccountID = %d and ServerPort = %d".formatted(id,port);
            boolean found;
            try {
                ResultSet resultSet = statement.executeQuery(firstQuery);
                found = (resultSet !=null && resultSet.next());
                StringBuilder account = new StringBuilder();
                if(found) {
                    account.replace(0,account.length(),"firstAccountTimestamp");
                } else {
                    ResultSet rs = statement.executeQuery(secondQuery);
                    found = (rs !=null && rs.next());
                    if(found) {
                        account.replace(0,account.length(),"secondAccountTimestamp");
                    } else {
                        System.out.println("You are not connected to that port");
                        return;
                    }
                }
                String update = "UPDATE serverDetail set %s = '%tF %tT' WHERE ServerPort = %d"
                        .formatted(account.toString(),timestamp,timestamp,port);
                statement.executeUpdate(update);
                connection.getConnection().close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Timestamp getTimestamp(int port) {
        Timestamp timestamp = new Timestamp(0);
        try {
            DataBaseConnection connection = new DataBaseConnection();
            Statement statement = connection.getConnection().createStatement();
            boolean firstAccount=false, secondAccount=false;
            String firstQuery = "SELECT AccountID FROM serverDetail WHERE AccountID = %d".formatted(id);
            String secondQuery = "SELECT AccountID FROM serverDetail WHERE SecondAccountID = %d".formatted(id);
            boolean found;
            try {
                ResultSet resultSet = statement.executeQuery(firstQuery);
                found = (resultSet != null && resultSet.next());
                StringBuilder account = new StringBuilder();
                if (found) {
                    firstAccount = true;
                    account.replace(0, account.length(), "firstAccountTimestamp");
                } else {
                    ResultSet rs = statement.executeQuery(secondQuery);
                    found = (rs != null && rs.next());
                    if (found) {
                        secondAccount = true;
                        account.replace(0, account.length(), "secondAccountTimestamp");
                    } else {
                        System.out.println("You are not connected to that port");
                    }
                }
                String timestampQuery = "SELECT %s FROM serverDetail WHERE ServerPort = %d".formatted(account.toString(),port);
                ResultSet rs = statement.executeQuery(timestampQuery);
                found = (rs!=null && rs.next());
                if(found) {
                    return rs.getTimestamp(account.toString());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return timestamp;
    }

    public void setMessage(int sentToID, int sentFromID,String content, int serverPort, Timestamp timestamp) {
        try {
            DataBaseConnection connection = new DataBaseConnection();
            Statement statement = connection.getConnection().createStatement();
            String query = "INSERT INTO messages (sentToID, sentFromID, content, ServerPort, messageTimestamp)\n" +
                    "VALUES (%d, %d, '%s', %d, '%tF %tT')".formatted(sentToID,sentFromID,content,serverPort,timestamp,timestamp);
            PreparedStatement preparedStatement = connection.getConnection().prepareStatement(query);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public LinkedList<Message> getMessage(int sentToID, int sentFromID, int serverPort) {
        LinkedList<Message> messages = new LinkedList<>();
        try{
            DataBaseConnection connection = new DataBaseConnection();
            Statement statement = connection.getConnection().createStatement();
            String query = "SELECT * FROM messages WHERE ServerPort = %d".formatted(serverPort);
            ResultSet resultSet = statement.executeQuery(query);
            while(resultSet.next()) {
                Message message = new Message(resultSet.getString("content"),
                        resultSet.getTimestamp("messageTimestamp"),
                        resultSet.getInt("sentFromID"));
                messages.add(message);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return messages;
    }


    public ClientFX(String username,String password) {
        this.username = username;
        this.password = password;
        friends = new ArrayList<>();
    }

    public boolean checkExistingFriend(Statement statement, String username, String table, String column, int id) {
        boolean found = false;
        String query = "SELECT * FROM %s WHERE %s = '%s' AND AccountID = %d"
                .formatted(table,column, username, id);
        try {
            ResultSet resultSet = statement.executeQuery(query);
            found = (resultSet !=null && resultSet.next());
        } catch (Exception e) {
            System.out.println("Ceva nu-i bun la query");
        }
        return found;
    }


    public boolean addFriend(String friend) {
        if(!Objects.equals(friend, getUsername())) {
            try {
                DataBaseConnection connection = new DataBaseConnection();
                Statement statement = connection.getConnection().createStatement();
                LoginController loginController = new LoginController();
                boolean exist;
                exist = loginController.checkExistingAccount(statement,friend,"accounts", "Username");
                if(exist) {
                    if(checkExistingFriend(statement,friend,"friends", "FriendUsername",getId())) {
                        System.out.println("Prietenul deja exista");
                        return false;
                    }
                    else {
                        try {
                            String query = "INSERT INTO friends (AccountID, FriendUsername) VALUES (?, ?)";
                            PreparedStatement psInsert = statement.getConnection().prepareStatement(query);
                            psInsert.setInt(1,getId());
                            psInsert.setString(2,friend);
                            psInsert.executeUpdate();
                            String selectQuery = "SELECT AccountID FROM accounts WHERE Username = '%s'".formatted(friend);
                            ResultSet rs = statement.executeQuery(selectQuery);
                            if (rs.next()) {
                                int idFriend = rs.getInt("AccountID");
                                psInsert = statement.getConnection().prepareStatement(query);
                                psInsert.setInt(1,idFriend);
                                psInsert.setString(2,getUsername());
                                psInsert.executeUpdate();
                                String serverQuery = "INSERT INTO serverDetail (AccountID,SecondAccountID, isOpen)" +
                                        "VALUES (%d, %d, FALSE)".formatted(id,idFriend);
                                psInsert = statement.getConnection().prepareStatement(serverQuery);
                                psInsert.executeUpdate();
                                String selectPort = "SELECT ServerPort FROM serverDetail WHERE AccountId = %d AND SecondAccountID = %d".formatted(id,idFriend);
                                ResultSet resultPort = statement.executeQuery(selectPort);
                                if(resultPort.next()) {
                                    int port = resultPort.getInt("ServerPort");
                                    ChatApplication.chatRooms.add(new ChatRoom(id,idFriend,port));
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("Error on inserting the friend into the database");
                        }
                        friends.add(friend);
                        System.out.println("A fost adaugat " + friend + " in lista prietenilor");
                    }
                } else {
                    System.out.println("Nu a fost gasit usernameul in baza de date");
                    return false;
                }
                connection.getConnection().close();
                return true;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            System.out.println("You cannot add yourself to the friendList");
            return false;
        }
    }

}
