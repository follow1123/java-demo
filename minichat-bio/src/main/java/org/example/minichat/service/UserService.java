package org.example.minichat.service;

import org.example.minichat.core.Message;
import org.example.minichat.core.UserSocket;
import org.example.minichat.model.FileModel;
import org.example.minichat.model.UserModel;
import org.example.minichat.utils.RandomUtil;
import org.example.minichat.utils.StrUtil;

import java.util.*;

public class UserService {

    private final UserModel userModel;
    private final FileModel fileModel;
    private final Map<String, UserSocket> userMap = new HashMap<>();
    private final Map<String, List<String>> chatHistories = new HashMap<>();

    public UserService(UserModel userModel, FileModel fileModel) {
        this.userModel = userModel;
        this.fileModel = fileModel;
    }

    public void handleLogin(UserSocket us, Message message) {
        String respStr = null;
        String body = message.getBody();
        if (body.contains(",")) {
            String[] usernameAndPassword = body.split(",");
            String username = usernameAndPassword[0];
            String password = usernameAndPassword[1];
            if (username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
                us.setName(username);
                String passwd = userModel.getProp(username);
                if (passwd != null && passwd.equals(password)) {
                    userMap.put(username, us);
                    respStr = RandomUtil.getRandStr(5);
                }
            }
        }
        System.out.printf("user '%s' %s\n", us.getName(), respStr == null ? "login failed!" : "is login");
        System.out.println("users = " + userMap.keySet());

        Message respMsg = Message.fromString(respStr);
        if (!message.getSerialNum().isEmpty()) {
            respMsg.setSerialNum(message.getSerialNum());
        }
        us.getMsgBus().sendMsg(respMsg);
    }

    public void handleUsers(UserSocket us, Message message) {
        Iterator<Map.Entry<String, UserSocket>> iterator = userMap.entrySet().iterator();
        StringBuilder sb = new StringBuilder();
        while (iterator.hasNext()) {
            Map.Entry<String, UserSocket> entry = iterator.next();
            String username = entry.getKey();
            UserSocket userSocket = entry.getValue();
            if (userSocket.isDisconnected()) {
                iterator.remove();
                continue;
            }
            if (!us.getName().equals(username)) {
                sb.append(username).append(",");
            }
        }
        Message respMsg = Message.getInstance();
        if (!sb.isEmpty()) {
            respMsg.setBody(sb.substring(0, sb.length() - 1));
        }

        if (!message.getSerialNum().isEmpty()) {
            respMsg.setSerialNum(message.getSerialNum());
        }
        us.getMsgBus().sendMsg(respMsg);
    }

    public void handleChatHistory(UserSocket us, Message message) {
        String friendName = message.getBody();
        String username = us.getName();
        String key = hasChat(username, friendName);
        List<String> histories = key == null ? new ArrayList<>() : chatHistories.get(key);

        StringBuilder sb = new StringBuilder();
        Message respMsg = Message.getInstance();

        for (String history : histories) sb.append(history).append("\n");

        if (!sb.isEmpty()) respMsg.setBody(sb.substring(0, sb.length() - 1));
        if (!message.getSerialNum().isEmpty()) respMsg.setSerialNum(message.getSerialNum());

        us.getMsgBus().sendMsg(respMsg);
    }

    public void handleSendMessage(UserSocket us, Message message) {
        String body = message.getBody();
        String[] bodyStr = body.split("\n");
        String friendName = bodyStr[0];
        String chatMessage = bodyStr[1];
        String username = us.getName();
        addChat(username, friendName, chatMessage);
    }

    private void addChat(String username, String friendName, String chatMessage) {
        String key = hasChat(username, friendName);
        if (key == null) {
            List<String> histories = new ArrayList<>();
            histories.add(StrUtil.joinWith(":", username, chatMessage));
            chatHistories.put(StrUtil.joinWith(":", username, friendName), histories);
        } else {
            chatHistories.get(key).add(StrUtil.joinWith(":", username, chatMessage));
        }
    }

    private String hasChat(String username, String friendName) {
        String key1 = StrUtil.joinWith(":", username, friendName);
        String key2 = StrUtil.joinWith(":", friendName, username);
        if (chatHistories.containsKey(key1)) {
            return key1;
        } else if (chatHistories.containsKey(key2)) {
            return key2;
        } else {
            return null;
        }
    }

    public void handleUploadFile(UserSocket us, Message message) {
        String body = message.getBody();
        String[] bodyArr = body.split("\n");
        String friendName = bodyArr[0];
        String filename = bodyArr[1];
        String base64File = bodyArr[2];
        String uuid = RandomUtil.randomUUID();
        Message respMsg = Message.getInstance();
        if (!message.getSerialNum().isEmpty()) respMsg.setSerialNum(message.getSerialNum());
        boolean saved = fileModel.storeFile(uuid, filename, base64File);
        if (saved) {
            respMsg.setBody("1");
            String chatMsg = StrUtil.joinWith(":", filename, uuid);
            addChat(us.getName(), friendName, chatMsg);
        }
        us.getMsgBus().sendMsg(respMsg);
    }

    public void handleDownloadFile(UserSocket us, Message message) {
        String fileUUID = message.getBody();
        String base64File = fileModel.getFile(fileUUID);

        Message respMsg = Message.fromString(base64File);
        if (!message.getSerialNum().isEmpty()) respMsg.setSerialNum(message.getSerialNum());
        us.getMsgBus().sendMsg(respMsg);
    }
}
