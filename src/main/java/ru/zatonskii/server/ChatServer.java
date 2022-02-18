package ru.zatonskii.server;

import ru.zatonskii.client.AuthenticationService;
import ru.zatonskii.client.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;


public class ChatServer {
    private final AuthenticationService authenticationService;
    private final Set<ClientHandler> loggedClients;

    public ChatServer() {
        authenticationService = new AuthenticationService();
        loggedClients = new HashSet<>();

        try {
            ServerSocket socket = new ServerSocket(6363);
            System.out.println("The server is running ...");
            while (true) {
                System.out.println("The server is waiting for connection...");
                Socket clientSocket = socket.accept();
                new ClientHandler(clientSocket, this);
            }
        } catch (IOException e) {
            throw new ChatServerException("Something went wrong", e);
        }
    }

    public AuthenticationService getAuthenticationService() {
        return authenticationService;
    }

    public void broadcast(String message) {
        for (ClientHandler clientHandler : loggedClients) {
             clientHandler.sendMessage(message);
        }
    }

    public void subscribe(ClientHandler clientHandler) {
        loggedClients.add(clientHandler);
    }

    public void unsubscribe(ClientHandler clientHandler) {
        loggedClients.remove(clientHandler);
    }

    public boolean isLogin(String name) {
//        Iterator<ClientHandler> iterator = loggedClients.iterator();
//        while (iterator.hasNext()) {
//            ClientHandler client = iterator.next();
//            if (client.getName().equals(name)) {
//                return true;
//            }
//        }
//        return false;
        return loggedClients.stream()
                .filter(client -> client.getName().equals(name))
                .findFirst()
                .isPresent();
    }
}
