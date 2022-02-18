package ru.zatonskii.client;

import ru.zatonskii.server.ChatServer;
import ru.zatonskii.server.ChatServerException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Optional;

public class ClientHandler {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String name;

    public ClientHandler(Socket socket, ChatServer chatServer) {
        this.socket = socket;
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new ChatServerException("Something wont wrong during client establishing.", e);
        }
        doAuthentication(chatServer);
        // слушать
    }

    public String getName() {
        return name;
    }

    private void doAuthentication(ChatServer chatServer) {
        while (true) {
            try {
                String message = in.readUTF();
                if (message.startsWith("-auth")) {
                    String[] credentialsStruct = message.split("\\s");
                    String login = credentialsStruct[1];
                    String password = credentialsStruct[2];

                    Optional<AuthenticationService.Entry> mayBeCredentials = chatServer.getAuthenticationService()
                            .findEntryByCredentials(login, password);
                    if (mayBeCredentials.isPresent()) {
                        AuthenticationService.Entry credentials = mayBeCredentials.get();
                        if (!chatServer.isLogin(credentials.getName())) {

                        } else {
                            sendMessage(String.format("User with name %s is already logged in", credentials.getName()));
                        }
                    } else {
                        sendMessage("Incorrect login or password");
                    }
                }
            } catch (IOException e) {
                throw new ChatServerException("Something wont wrong during client authentication.", e);
            }
        }
    }
    public void in() {

    }
    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            throw new ChatServerException("Something wont wrong during sending message.", e);
        }
    }
}
