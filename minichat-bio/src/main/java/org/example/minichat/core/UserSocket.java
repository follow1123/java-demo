package org.example.minichat.core;

import org.example.minichat.service.UserService;

import java.net.Socket;
import java.util.concurrent.ThreadPoolExecutor;

public class UserSocket {

    private final MsgBus msgBus;
    private final UserService userService;
    private String name;

    public UserSocket(Socket socket, ThreadPoolExecutor socketHandler, UserService userService) {
        this.userService = userService;
        this.msgBus = new MsgBus(socket, socketHandler);
        this.msgBus.setReadTask(this::handleMessage);
    }

    public void handleMessage(Message message) {
        if (message.getToken().isEmpty()) {
            userService.handleLogin(this, message);
            return;
        }
        if (MsgType.LOGIN.equals(message.getMsgType())) {
            userService.handleLogin(this, message);
        } else if (MsgType.USERS.equals(message.getMsgType())) {
            userService.handleUsers(this, message);
        } else if (MsgType.CHAT_HISTORY.equals(message.getMsgType())) {
            userService.handleChatHistory(this, message);
        } else if (MsgType.SEND_MESSAGE.equals(message.getMsgType())) {
            userService.handleSendMessage(this, message);
        } else if (MsgType.UPLOAD_FILE.equals(message.getMsgType())) {
            userService.handleUploadFile(this, message);
        } else if (MsgType.DOWNLOAD_FILE.equals(message.getMsgType())) {
            userService.handleDownloadFile(this, message);
        } else {
            System.err.println("error message type");
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MsgBus getMsgBus() {
        return msgBus;
    }

    public boolean isDisconnected() {
        return msgBus.isDisconnected();
    }
}
