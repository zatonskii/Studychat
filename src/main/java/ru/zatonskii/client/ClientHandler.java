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
    private ChatServer chatServer;
    private String name;

    public ClientHandler(Socket socket, ChatServer chatServer) {
        this.socket = socket;
        this.chatServer = chatServer;
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new ChatServerException("Something wont wrong during client establishing.", e);
        }

        new Thread(() -> {
            doAuthentication();
            listen(); // слушать
        })
                .start();
    }

    public String getName() {
        return name;
    }

    public void listen() {
        receiveMessage();
    }

    private void doAuthentication() {
        sendMessage("Welcome to chat! Please, do authentication. ");
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
                            name = credentials.getName();
                            chatServer.broadcast(String.format("The user [%s] has entered the chat", name));
                            chatServer.subscribe(this);
                            break;
                        } else {
                            sendMessage(String.format("User with name %s is already logged in", credentials.getName()));
                        }
                    } else {
                        sendMessage("Incorrect login or password");
                    }
                } else {
                    sendMessage("Incorrect authentication message. " +
                            " Please use true command!:-auth yor_login yor_password ");
                }
            } catch (IOException e) {
                throw new ChatServerException("Something wont wrong during client authentication.", e);
            }
        }
    }
    // Тут мы получаем сообщения и что-то с ними делаем
    public void receiveMessage() {
        while (true) {
            try {
                String message = in.readUTF();
                chatServer.broadcast(message);
            } catch (IOException e) {
                throw new ChatServerException("Something wont wrong during receiving the message.", e);
            }
        }
    }
    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            throw new ChatServerException("Something wont wrong during sending the message.", e);
        }
    }
}
