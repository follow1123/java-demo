package org.example.minichat.core;

import org.example.minichat.model.FileModel;
import org.example.minichat.model.UserModel;
import org.example.minichat.service.UserService;
import org.example.minichat.utils.SocketUtil;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Server {

    private ServerSocket serverSocket;
    private final ThreadPoolExecutor socketHandler = (ThreadPoolExecutor) Executors.newFixedThreadPool(6);
    private boolean running = true;
    private UserService userService;

    public void start() {
        try {
            int port = 8989;
            serverSocket = new ServerSocket(port);
            System.out.printf("server started listening %s\n", port);
            UserModel userModel = new UserModel();
            FileModel fileModel = new FileModel();
            this.userService = new UserService(userModel, fileModel);
            while (running) {
                if (serverSocket.isClosed()) running = false;
                Socket socket = serverSocket.accept();
                System.out.printf("client %s is connected\n", socket.getInetAddress().getHostAddress());
                new UserSocket(socket, socketHandler, userService);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            SocketUtil.close(serverSocket);
        }
    }
}
