package cn.y.java.minichat.utils;

import java.io.Closeable;
import java.io.IOException;

public class SocketUtil {

    public static void close(Closeable closeable){
        if (closeable != null){
            try {
                closeable.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
