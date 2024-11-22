package cn.y.java.minichat.core;

import cn.y.java.minichat.core.ui.UILauncher;
import cn.y.java.minichat.utils.SocketUtil;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    public void start() {
        Socket socket = null;
        try {
            InetAddress address = InetAddress.getLocalHost();
            int port = 8989;
            socket = new Socket(address, port);
            System.out.printf("connect to %s on %d\n", address.getHostAddress(), port);
            MsgBus msgBus = new MsgBus(socket);
            new UILauncher(msgBus).start();
        } catch (IOException e) {
            System.out.printf("connect failed!, %s\n", e.getMessage());
        } finally {
            SocketUtil.close(socket);
        }
    }
}
