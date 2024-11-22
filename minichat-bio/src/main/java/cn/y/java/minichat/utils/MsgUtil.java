package cn.y.java.minichat.utils;

import cn.y.java.minichat.core.Message;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MsgUtil {

    private static void checkDelimiter(int c){if (c != '&') throw new RuntimeException("error message");}

    private static void checkHeader(InputStream is) throws IOException {
        int headIdx = 0;
        int c;
        while ((c = is.read()) != -1 && headIdx < Message.HEADER.length()) {
            if (Message.HEADER.charAt(headIdx) == c) {
                headIdx++;
            } else {
                headIdx = 0;
            }
        }
        checkDelimiter(c);
    }

    private static String getString(InputStream is) throws IOException {
        byte[] data = new byte[5];
        int i;
        int c;
        for (i = 0; (c = is.read()) != -1 && c != Message.DELIMITER; i++) {
            data[i] = (byte) c;
        }
        checkDelimiter(c);
        return new String(data, 0, i);
    }
    private static int getMsgBlock(byte[] data, int start){
        int s = start;
        while (data[s] != Message.DELIMITER){
            s++;
        }
        return s - start;
    }

    private static void checkEnd(byte[] data){
        int idx = data.length;
        if (data[--idx] != 'm' || data[--idx] != 'o' || data[--idx] != 'e')
            throw new RuntimeException("error message");
    }

    public static Message readMsg(InputStream is) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(is);
        checkHeader(bis);

        int len = Integer.parseInt(getString(bis));
        byte[] data = new byte[len];
        int readLen = bis.read(data);
        if (readLen != len) throw new RuntimeException("error message");
        checkEnd(data);

        Message message = Message.getInstance();
        String[] datagrams = message.getDatagrams();

        for (int i = 0, s = 0, l = getMsgBlock(data, s);
             i < datagrams.length;
             i++, s += l, l = getMsgBlock(data, s)) {
            if (i == datagrams.length - 1){
                datagrams[i] = new String(data, s, data.length - s - Message.END_OF_MSG.length() - 1);
                break;
            }
            datagrams[i] = new String(data, s, l++);
        }
        return message;
    }

    public static void writeMsg(Message msg, OutputStream os) throws IOException {
        os.write(msg.encodeBytes());
        os.flush();
    }
}
