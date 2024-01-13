package com.taoyiyi.chat.client;

import com.google.protobuf.ByteString;
import com.taoyiyi.chat.proto.TalkToOther;
import com.taoyiyi.chat.proto.TransportMessageOuterClass;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Slf4j
public class ClientTest {


    @Test
    public void user1Chat() {
        ChatClient.get().setUserCode(TestLocalMsgConstant.fromUser).checkConnect();

        sendMsg(TestLocalMsgConstant.fromUser,TestLocalMsgConstant.toUser);
    }


    @Test
    public void u2Chat() {
        ChatClient.get().setUserCode(TestLocalMsgConstant.toUser).checkConnect();
        sendMsg(TestLocalMsgConstant.toUser,TestLocalMsgConstant.fromUser);
    }


    public void sendMsg(String formUser, String toUser) {


        String userInput;
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            try {
                if ((userInput = stdIn.readLine()) == null) {
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    log.info("nettyT , : {}", userInput);
                } else {
                    log.info("nettyT , : {}", userInput);
                    // 发送一个信息
                    TalkToOther.TalkToOtherMessage.Builder talkBuild = TalkToOther.TalkToOtherMessage.newBuilder().setToUser(toUser)
                            .setFormUser(formUser)
                            .setContentType(TransportMessageOuterClass.EnumContentType.Text)
                            .setContent(ByteString.copyFrom(userInput.getBytes(StandardCharsets.UTF_8)));
                    ChatClient.get().msgSender.sendMsgSync(TransportMessageOuterClass.EnumMsgType.TalkToOther, talkBuild.build(), true);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (userInput.equals("exit")) {
                break;
            }
        }

    }


}
