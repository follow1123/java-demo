package org.example.minichatjavaweb.utils;

public class RestfulUtil {

    public static String getPathVariable(String uri, int startIdx, int paramIdx) {
        if (uri.length() < startIdx) return null;
        byte[] uriBytes = uri.getBytes();
        int idx = 0;
        int paramLen = 0;
        int pos = startIdx;

        for (; pos < uriBytes.length; pos++) {
            if (uriBytes[pos] == '/') {
                if (++idx == paramIdx) break;
                paramLen = 0;
                continue;
            }
            paramLen++;
        }

        if (pos == uriBytes.length && idx - paramIdx != -1) return null;

        byte[] param = new byte[paramLen];
        System.arraycopy(uriBytes, pos - paramLen, param, 0, param.length);
        return new String(param);
    }
}
