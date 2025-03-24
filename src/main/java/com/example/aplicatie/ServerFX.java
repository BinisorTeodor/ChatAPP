package com.example.aplicatie;

import javafx.scene.layout.VBox;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerFX {

    private ServerSocket serverSocket;

    public ServerFX(ServerSocket serverSocket) {
        try {
            this.serverSocket = serverSocket;
        } catch (Exception e) {
            System.out.println("Error creating the server");
            throw new RuntimeException(e);
        }
    }

    public void start() throws IOException {
        try {
            ArrayList<ClientFXHandler> clientFXHandlers = new ArrayList<>();
            System.out.println(serverSocket.isBound());
            while (!serverSocket.isClosed()) {

                Socket socket = serverSocket.accept();
                System.out.println(serverSocket.isBound());
                ClientFXHandler clientFXHandler = new ClientFXHandler(socket , clientFXHandlers);
                System.out.println("A new client has connected");
                Thread thread = new Thread(clientFXHandler);
                thread.start();
            }
        } catch (Exception e) {
            System.out.println("Error starting the server...");
            closeServer();
        }
    }

    private void closeServer() {
        try {
            if(serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void startGUI(int port) throws IOException {
        ServerFX server = new ServerFX(new ServerSocket(port));
        server.start();
    }
}
