package com.example.aplicatie;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

    private Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private String username;

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.username = username;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void messageToSend() {
        Scanner scanner = new Scanner(System.in);
            try {
                bufferedWriter.write(username);
                bufferedWriter.newLine();
                bufferedWriter.flush();
                while (socket.isConnected()) {
                    String message = scanner.nextLine();
                    bufferedWriter.write("                [" + username + "]: " + message);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeEveryThing(socket, bufferedWriter, bufferedReader);
            }
    }

    private void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (socket.isConnected()) {
                    try {
                        String messageToReceive = bufferedReader.readLine();
                        System.out.println(messageToReceive);
                    } catch (IOException e) {
                        closeEveryThing(socket, bufferedWriter, bufferedReader);
                    }
                }
            }
        }).start();
    }

    private void closeEveryThing(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader) {
        try {
            if (socket != null) {
                socket.close();
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    Scanner scanner = new Scanner(System.in);
                    System.out.println("Please enter your username: ");
                    String username = scanner.nextLine();
                    Socket socket = new Socket("localhost", 1234);
                    Client client = new Client(socket, username);
                    client.listenForMessage();
                    client.messageToSend();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();


    }
}
