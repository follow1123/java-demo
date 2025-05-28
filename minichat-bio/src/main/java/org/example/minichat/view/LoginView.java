package org.example.minichat.view;

import org.example.minichat.core.ClientContext;
import org.example.minichat.core.Message;
import org.example.minichat.core.MsgType;
import org.example.minichat.core.ui.TextView;

import java.util.Scanner;

public class LoginView extends TextView {

    private final Scanner scanner;
    private final ClientContext context;

    public LoginView(Scanner scanner, ClientContext context) {
        lines = new String[]{"please input username and password", "eg: username,password"};
        this.scanner = scanner;
        this.context = context;
    }

    private void onInput(String input) {
        Message message = Message.fromString(input);
        message.setMsgType(MsgType.LOGIN);
        Message result = context.getMsgBus().sendMsgBlocked(message);
        String token = result.getBody();
        if (token != null && !token.isEmpty()) {
            context.setUsername(input.split(",")[0]);
            context.setToken(token);
            context.nextView();
        }
        System.out.printf("login %s\n", context.getToken() == null ? "failed!" : "succeed!");
    }

    @Override
    public void render() {
        super.render();
        this.onInput(scanner.nextLine());
    }
}
