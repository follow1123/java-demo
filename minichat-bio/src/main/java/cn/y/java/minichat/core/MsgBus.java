package cn.y.java.minichat.core;

import cn.y.java.minichat.utils.MsgUtil;
import cn.y.java.minichat.utils.RandomUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;

public class MsgBus {

    private final Socket socket;
    private final HashMap<String, Consumer<Message>> readMap;
    private final LinkedList<Message> writeQueue;
    // 默认处理消息的方式
    private Consumer<Message> readTask;
    private final ThreadPoolExecutor socketHandler;
    private final boolean outerHandler;

    private static final long DEFAULT_BLOCKED_SECONDS = 10;

    public MsgBus(Socket socket, ThreadPoolExecutor socketHandler) {
        if (socket == null) throw new RuntimeException("socket not be null");
        if (socketHandler == null){
            this.socketHandler = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
            this.outerHandler = false;
        }else {
            this.socketHandler = socketHandler;
            this.outerHandler = true;
        }
        this.readMap = new HashMap<>();
        this.writeQueue = new LinkedList<>();
        this.socket = socket;
        this.readTask = message -> System.out.printf("receive message %s\n", message);
        this.socketHandler.execute(this::handleRead);
        this.socketHandler.execute(this::handleWrite);
    }

    public MsgBus(Socket socket){this(socket, null);}

    public void setReadTask(Consumer<Message> readTask) {this.readTask = readTask;}

    public void sendMsg(Message message){
        assertConnected();
        synchronized (writeQueue){
            writeQueue.addFirst(message);
            writeQueue.notify();
        }
    }

    public Message sendMsgBlocked(Message message){
        assertConnected();
        String serialNum = RandomUtil.randomUUID();
        message.setSerialNum(serialNum);

        Message[] m = new Message[1];
        Consumer<Message> consumer = msg -> m[0] = msg;
        readMap.put(serialNum, consumer);
        synchronized (consumer){
            sendMsg(message);
            try {
                consumer.wait(getBlockMills());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        if (m[0] == null) throw new RuntimeException("no message response");
        return m[0];
    }

    public void sendMsg(Message message, Consumer<Message> onReceive){
        assertConnected();
        String serialNum = RandomUtil.randomUUID();
        message.setSerialNum(serialNum);
        sendMsg(message);
        readMap.put(serialNum, onReceive);
    }

    private void handleRead() {
        System.out.printf("start handleRead, pool{active: %d, tasks: %d, completedTasks: %d}\n", socketHandler.getActiveCount(), socketHandler.getTaskCount(), socketHandler.getCompletedTaskCount());
        while (isConnected()){
            try {
                InputStream is = socket.getInputStream();
                Message message = MsgUtil.readMsg(is);

                System.out.printf("message received %s\n", message);
                // 判断是否需要单独处理消息
                String serialNum = message.getSerialNum();
                if (serialNum.isEmpty() || !readMap.containsKey(serialNum)){
                    readTask.accept(message);
                    continue;
                }

                Consumer<Message> consumer = readMap.get(serialNum);
                synchronized (consumer) {
                    consumer.accept(message);
                    consumer.notify();
                }
            } catch (SocketException e){
                // 判断socket是否断开
                System.out.printf("socket %s is disconnected: %s\n", socket.getInetAddress().getHostAddress(), e.getMessage());
                disconnect();
            } catch (IOException e) {
                System.out.printf("read message error: %s\n", e.getMessage());
            }
        }
        System.out.printf("stop handleRead, pool{active: %d, tasks: %d, completedTasks: %d}\n", socketHandler.getActiveCount(), socketHandler.getTaskCount(), socketHandler.getCompletedTaskCount());
    }

    private void handleWrite() {
        System.out.printf("start handleWrite, pool{active: %d, tasks: %d, completedTasks: %d}\n", socketHandler.getActiveCount(), socketHandler.getTaskCount(), socketHandler.getCompletedTaskCount());
        while (isConnected()) {
            synchronized (writeQueue) {
                try {
                    if (writeQueue.isEmpty()) writeQueue.wait(getBlockMills());
                    if (writeQueue.isEmpty()) continue;

                    Message message = writeQueue.removeLast();
                    System.out.printf("send message %s\n", message);
                    MsgUtil.writeMsg(message, socket.getOutputStream());
                } catch (SocketException e){
                    // 判断socket是否断开
                    System.out.printf("socket %s is disconnected: %s\n", socket.getInetAddress().getHostAddress(), e.getMessage());
                    disconnect();
                } catch (IOException e) {
                    System.out.printf("write message error: %s\n", e.getMessage());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        System.out.printf("stop handleWrite, pool{active: %d, tasks: %d, completedTasks: %d}\n", socketHandler.getActiveCount(), socketHandler.getTaskCount(), socketHandler.getCompletedTaskCount());
    }

    private long getBlockMills(){
        return DEFAULT_BLOCKED_SECONDS * 1000;
    }

    public void disconnect(){
        try {
            System.out.println("manual stop socket");
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        synchronized (writeQueue){
            writeQueue.notifyAll();
        }
        for (Consumer<Message> value : readMap.values()) {
           synchronized (value){
               value.notifyAll();
           }
        }
        shutdownHandler();
    }

    private void assertConnected(){
       if (isDisconnected()) throw new RuntimeException("socket is closed");
    }

    public void shutdownHandler(){
        if (!outerHandler) socketHandler.shutdown();
    }

    public boolean isConnected(){
        return !isDisconnected();
    }

    public boolean isDisconnected(){
        return socket.isClosed();
    }
}
