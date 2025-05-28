package org.example.minichat.view;


import org.example.minichat.core.ClientContext;
import org.example.minichat.core.Message;
import org.example.minichat.core.MsgType;
import org.example.minichat.core.ui.RoundedBorder;
import org.example.minichat.core.ui.TextView;
import org.example.minichat.core.ui.View;
import org.example.minichat.utils.ResourcesUtil;
import org.example.minichat.utils.StrUtil;

import java.io.*;
import java.util.*;

public class ChatView implements View {

    private final Scanner scanner;
    private final ClientContext context;
    private final List<TextView> viewList = new ArrayList<>();
    private final Map<String, Runnable> keyMap = new HashMap<>();
    private String friendName;
    private boolean sendFileMode;

    private final String userFilesDir;

    public static final int MAX_CHAT_HISTORY = 6;

    public ChatView(Scanner scanner, ClientContext context) {
        this.scanner = scanner;
        this.context = context;
        this.userFilesDir = ResourcesUtil.getRootPath() + "userfiles";
        File file = new File(userFilesDir);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    private void beforeRender() {
        viewList.clear();
        keyMap.clear();
        friendName = context.getData("friendName");
        Message msg = Message.getInstance();
        msg.setToken(context.getToken());
        msg.setMsgType(MsgType.CHAT_HISTORY);
        msg.setBody(friendName);
        Message respMsg = context.getMsgBus().sendMsgBlocked(msg);
        String historyStr = respMsg.getBody();
        TextView titleView = new TextView(String.format("chat with %s", friendName));
        titleView.setTextDirection(TextView.CENTER);
        viewList.add(titleView);

        int fileButtonId = 0;
        if (!historyStr.isEmpty()) {
            String[] historyArr = historyStr.split("\n");
            int historySize = Math.min(ChatView.MAX_CHAT_HISTORY, historyArr.length);
            for (int i = historyArr.length - historySize; i < historyArr.length; i++) {
                String chatStr = historyArr[i];
                String[] chatArr = chatStr.split(":");
                String msgOwner = chatArr[0];
                String chatMsg = chatArr[1];
                String[] prompts = {chatMsg};
                if (chatArr.length > 2) {
                    String fileId = chatArr[2];
                    prompts = new String[]{
                            String.format("upload file: %s", chatMsg),
                            String.format("type %d to download", fileButtonId)
                    };
                    keyMap.put(fileButtonId + "", () -> {
                        String s = downloadFile(fileId, chatMsg);
                        System.out.printf("file download to: %s\n", s);
                    });
                }

                TextView textView = new TextView(prompts);
                textView.setBorderWrapText(true);
                textView.setTextDirection(context.getUsername().equals(msgOwner) ? TextView.RIGHT : TextView.LEFT);
                textView.setBorder(new RoundedBorder());
                viewList.add(textView);
            }
        }
        if (sendFileMode) {
            viewList.add(new TextView("[r]efresh [m]essage (type a file path to upload)"));
            keyMap.put("m", () -> this.sendFileMode = false);
        } else {
            viewList.add(new TextView("[r]efresh [f]ile (type message to send)"));
            keyMap.put("f", () -> this.sendFileMode = true);
        }


        keyMap.put("r", this::render);
    }

    private void onInput(String input) {
        if (input != null && !input.isEmpty()) {
            Message message = Message.getInstance();
            message.setToken(context.getToken());

            if (sendFileMode) {
                boolean state = sendFile(message, input);
                if (!state) {
                    System.out.printf("file '%s' invalid or upload failed!", input);
                    this.render();
                }
                return;
            } else {
                message.setMsgType(MsgType.SEND_MESSAGE);
                message.setBody(StrUtil.joinWith("\n", friendName, input));
            }
            context.getMsgBus().sendMsg(message);
        }
        this.render();
    }

    private String downloadFile(String fileId, String filename) {
        Message message = Message.fromString(fileId);
        message.setToken(context.getToken());
        message.setMsgType(MsgType.DOWNLOAD_FILE);
        Message respMsg = context.getMsgBus().sendMsgBlocked(message);
        String base64File = respMsg.getBody();
        File file = new File(userFilesDir, filename);
        ByteArrayInputStream bais = new ByteArrayInputStream(Base64.getDecoder().decode(base64File));
        try (FileOutputStream fos = new FileOutputStream(file)) {
            byte[] data = new byte[1024];
            int len;
            while ((len = bais.read(data)) != -1) {
                fos.write(data, 0, len);
            }
            fos.flush();
            return file.getAbsolutePath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private boolean sendFile(Message message, String path) {
        if (path == null || path.isEmpty()) return false;
        File file = new File(path);
        if (!file.exists() || file.isDirectory() || file.length() > 10 * 1024 * 1024) return false;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] data = new byte[1024];
            int len;
            while ((len = fis.read(data)) != -1) {
                baos.write(data, 0, len);
            }
            baos.flush();
            Base64.Encoder encoder = Base64.getEncoder();
            byte[] encode = encoder.encode(baos.toByteArray());
            message.setMsgType(MsgType.UPLOAD_FILE);
            message.setBody(StrUtil.joinWith("\n", friendName, file.getName(), new String(encode)));
            Message respMsg = context.getMsgBus().sendMsgBlocked(message);
            return respMsg.getBody().equals("1");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void render() {
        this.beforeRender();

        for (TextView textView : viewList) {
            textView.render();
        }
        String line = scanner.nextLine();
        if (keyMap.containsKey(line)) {
            keyMap.get(line).run();
        } else {
            this.onInput(line);
        }
    }
}