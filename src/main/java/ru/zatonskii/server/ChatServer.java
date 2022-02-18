package ru.zatonskii.server;

import ru.zatonskii.client.AuthenticationService;
import ru.zatonskii.client.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class ChatServer {
    private final AuthenticationService authenticationService;
    private final Set<ClientHandler> loggedClients;

    public ChatServer() {
        authenticationService = new AuthenticationService();
        loggedClients = new HashSet<>();

        try {
            ServerSocket socket = new ServerSocket(6363);
            Socket clientSocket = socket.accept();
        } catch (IOException e) {
            throw new ChatServerException("Something went wrong", e);
        }
    }

    public AuthenticationService getAuthenticationService() {
        return authenticationService;
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
