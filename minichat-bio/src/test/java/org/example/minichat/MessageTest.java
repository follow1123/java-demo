package org.example.minichat;

import org.example.minichat.core.Message;
import org.example.minichat.utils.MsgUtil;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MessageTest {

    @ParameterizedTest
    @CsvSource({
            "qwerjk, 132413, aerw, a, header&24&qwerjk&132413&aerw&a&eom",
            "135uERw, 9, '', '213\n77\t3\r', 'header&24&135uERw&9&&213\n77\t3\r&eom'",
            "'', '', '', b, header&8&&&&b&eom"
    })
    public void testEncodeMsg(String token, String serialNum, String msgType, String body, String excepted) {
        Message message = Message.fromString(body);
        message.setToken(token);
        message.setSerialNum(serialNum);
        message.setMsgType(msgType);
        assertEquals(excepted, message.encodeDatagram());
    }

    @ParameterizedTest
    @CsvSource({
            "header&24&qwerjk&132413&aerw&a&eom, a",
            "'header&24&135uERw&9&&213\n77\t3\r&eom', '213\n77\t3\r'",
            "header&8&&&&b&eom, b"
    })
    public void testReadMsg(String datagram, String excepted) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(datagram.getBytes());
        assertEquals(excepted, MsgUtil.readMsg(bais).getBody());
    }

    @ParameterizedTest
    @CsvSource({
            "qwerjk, 132413, aerw, a, header&24&qwerjk&132413&aerw&a&eom",
            "135uERw, 9, '', '213\n77\t3\r', 'header&24&135uERw&9&&213\n77\t3\r&eom'",
            "'', '', '', b, header&8&&&&b&eom"
    })
    public void testWriteMsg(String token, String serialNum, String msgType, String body, String excepted) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Message message = Message.fromString(body);
        message.setToken(token);
        message.setSerialNum(serialNum);
        message.setMsgType(msgType);
        MsgUtil.writeMsg(message, baos);
        assertEquals(excepted, baos.toString());
    }

}
