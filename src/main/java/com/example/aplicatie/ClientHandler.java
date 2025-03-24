package com.example.aplicatie;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{

    public ArrayList<ClientHandler> clientHandlers;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;

    public ClientHandler(Socket socket, ArrayList<ClientHandler> clientHandlers) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.clientUsername = bufferedReader.readLine();
            this.clientHandlers = clientHandlers;
            clientHandlers.add(this);
            broadcastMessage("Server : " + clientUsername + " has connected");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        while(socket.isConnected()) {
            try {
                String messageFromClient;

                    messageFromClient = bufferedReader.readLine();
                    broadcastMessage(messageFromClient);

            } catch (Exception e) {
                closeEveryThing(socket,bufferedWriter,bufferedReader);
                break;
            }
        }
    }

    private void broadcastMessage(String messageToSend) {

        try {
            for(ClientHandler clientHandler: clientHandlers) {
                if(!clientHandler.equals(this)) {
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            }
        } catch (Exception e) {
            closeEveryThing(socket,bufferedWriter,bufferedReader);
        }

    }

    private void removeClientHandler() {
        clientHandlers.remove(this);
        broadcastMessage("SERVER : " + this.clientUsername + " has left the chat");
    }

    private void closeEveryThing(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader) {
        removeClientHandler();
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

}
