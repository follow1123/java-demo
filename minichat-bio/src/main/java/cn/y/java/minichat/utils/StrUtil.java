package cn.y.java.minichat.utils;

public class StrUtil {

    public static String joinWith(String symbol, String... strs){
        StringBuilder sb = new StringBuilder();
        for (String str : strs) {
            sb.append(str).append(symbol);
        }
        return sb.substring(0, sb.length() - symbol.length());
    }
}
