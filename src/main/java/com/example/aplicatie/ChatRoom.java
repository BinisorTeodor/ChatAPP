package com.example.aplicatie;

import java.io.IOException;
import java.net.ServerSocket;

public class ChatRoom {
    private int port;
    private int accountID;
    private int secondAccountID;

    public ChatRoom(int accountID, int secondAccountID, int port) throws IOException {
        this.port = port;
        this.secondAccountID = secondAccountID;
        this.accountID = accountID;
    }


    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public int getSecondAccountID() {
        return secondAccountID;
    }

    public void setSecondAccountID(int secondAccountID) {
        this.secondAccountID = secondAccountID;
    }

    @Override
    public String toString() {
        return (this.accountID + " " + this.secondAccountID + " " + this.port);
    }
}
