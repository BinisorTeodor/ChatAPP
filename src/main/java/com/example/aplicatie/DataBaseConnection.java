package com.example.aplicatie;

import com.mysql.cj.jdbc.MysqlDataSource;
import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.Statement;

public class DataBaseConnection  {

    private Connection connection;

    public Connection getConnection() {

        var dataSource = new MysqlDataSource();

        String Path = System.getenv("PATH");
        String Username = System.getenv("USERNAME");
        String Password = System.getenv("PASSWORD");
        dataSource.setURL(Path);
        connection = null;
        try   {
            connection = dataSource.getConnection(Username, Password);
        } catch (Exception e) {
            System.out.println("The connection to the database couldn't be done");
        }
        return connection;
    }


}
