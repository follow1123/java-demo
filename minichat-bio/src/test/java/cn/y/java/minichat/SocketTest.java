package cn.y.java.minichat;

import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

public class SocketTest {

    private final int port = 9999;

    private void startServer(){
        new Thread(() -> {
            try {
                ServerSocket ss = new ServerSocket(port);
                System.out.println("server started!");
                Socket s = ss.accept();

                Thread.sleep(2000);
                ss.close();
                System.out.println("server stopped!");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    @Test
    public void testConnectLater() throws Exception{
        startServer();
        Socket s = new Socket();
        assertFalse(s.isConnected());
        assertFalse(s.isBound());
        s.connect(new InetSocketAddress(InetAddress.getLocalHost(), port));
        assertTrue(s.isConnected());
        assertTrue(s.isBound());
    }

    @Test
    public void testClientStatus() throws Exception{
        startServer();
        Socket s = new Socket(InetAddress.getLocalHost(), port);
        assertFalse(s.isClosed());
        assertFalse(s.isInputShutdown());
        assertFalse(s.isOutputShutdown());

        System.out.println("shutdownInput");
        s.shutdownInput();
        assertFalse(s.isClosed());
        assertTrue(s.isInputShutdown());
        assertFalse(s.isOutputShutdown());

        System.out.println("shutdownOutput");
        s.shutdownOutput();
        assertFalse(s.isClosed());
        assertTrue(s.isInputShutdown());
        assertTrue(s.isOutputShutdown());

        System.out.println("close");
        s.close();
        assertTrue(s.isClosed());
        assertTrue(s.isInputShutdown());
        assertTrue(s.isOutputShutdown());
    }

    @Test
    public void testInputOutputStatueAfterClose() throws Exception{
        startServer();
        Socket s = new Socket(InetAddress.getLocalHost(), port);
        assertFalse(s.isClosed());
        assertFalse(s.isInputShutdown());
        assertFalse(s.isOutputShutdown());

        s.close();
        assertTrue(s.isClosed());
        assertFalse(s.isInputShutdown());
        assertFalse(s.isOutputShutdown());
    }
}
