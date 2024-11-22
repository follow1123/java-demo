package cn.y.java.minichat.core;

public class Message {

    // 常量
    public static final String HEADER = "header";
    public static final String END_OF_MSG = "eom";
    public static final char DELIMITER = '&';

    /* 报文体
        0 token
        1 serialNum
        2 msgType
        3 body
     */
    private final String[] datagrams = new String[4];

    // 构造器
    private Message() {}
    private Message(String body) {datagrams[3] = nullToEmpty(body);}

    public static Message getInstance() {return new Message();}
    public static Message fromString(String msg) {return new Message(msg);}
    public static Message fromObj(Object o){return fromString(o.toString());}

    public String getToken() {return nullToEmpty(datagrams[0]);}
    public void setToken(String token) {datagrams[0] = token;}
    public String getSerialNum() {return nullToEmpty(datagrams[1]);}
    public void setSerialNum(String serialNum) {datagrams[1] = serialNum;}
    public String getMsgType() {return nullToEmpty(datagrams[2]);}
    public void setMsgType(String msgType) {datagrams[2] = msgType;}
    public String getBody() {return nullToEmpty(datagrams[3]);}
    public void setBody(String body) {datagrams[3] = body;}
    public String[] getDatagrams(){return datagrams;}

    private String nullToEmpty(String str){return str == null ? "" : str;}
    public byte[] encodeBytes() {return encodeDatagram().getBytes();}
    public int getLength() {
        int len = END_OF_MSG.length() + datagrams.length;
        for (int i = 0; i < datagrams.length; i++) {
            len += nullToEmpty(datagrams[i]).length();
        }
        return len;
    }

    public String encodeDatagram(){
        StringBuilder sb = new StringBuilder();
        // 添加报文头
        sb.append(HEADER).append(DELIMITER);

        // 添加报文长度
        sb.append(getLength()).append(DELIMITER);
        // 添加报文体
        for (int i = 0; i < datagrams.length; i++) {
            sb.append(nullToEmpty(datagrams[i])).append(DELIMITER);
        }
        // 添加报文结束符
        sb.append(END_OF_MSG);
        return sb.toString();
    }

    @Override
    public String toString() {
        return encodeDatagram();
    }
}
