package com.example.aplicatie;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientFXHandler implements Runnable{

    public ArrayList<ClientFXHandler> clientFXHandlers;
    private ClientFX client;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

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

    public ClientFXHandler(Socket socket ,ArrayList<ClientFXHandler> clientFXHandlers) throws IOException {
        this.socket = socket;
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.clientFXHandlers = clientFXHandlers;
        clientFXHandlers.add(this);
        //broadcastMessage("Server : New guy" + " has connected");
    }

    private void broadcastMessage(String messageToSend) {

        try {
            for(ClientFXHandler clientFXHandler: clientFXHandlers) {
                if(!clientFXHandler.equals(this)) {
                    clientFXHandler.bufferedWriter.write(messageToSend);
                    clientFXHandler.bufferedWriter.newLine();
                    clientFXHandler.bufferedWriter.flush();
                }
            }
        } catch (Exception e) {
            closeEveryThing(socket,bufferedWriter,bufferedReader);
        }

    }

    private void removeClientHandler() {
        clientFXHandlers.remove(this);
        broadcastMessage("SERVER : This guy" + " has left the chat");
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
