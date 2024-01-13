package com.taoyiyi.chat.processor;


import com.taoyiyi.chat.proto.TransportMessageOuterClass;
import com.taoyiyi.chat.scoket.handler.MessageHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
public class SocketMessageProcessor {

    private static final String HANDLER = "Handler";

    @Autowired
    private Map<String, MessageHandler> messageHandlerMap;

    public void handler(ChannelHandlerContext channelHandlerContext, Object message) {
        log.info("handler , channelHandlerContext: {}, message: {}",channelHandlerContext,message);
        ;

        if (Objects.isNull(message)) {
            log.warn("message is null");
            return;
        }

        TransportMessageOuterClass.TransportMessage transportMessage = (TransportMessageOuterClass.TransportMessage) message;

        String msgType = transportMessage.getMsgType().toString();
        msgType = toLowerCaseFirstOne(msgType) + HANDLER;

        if (!messageHandlerMap.containsKey(msgType)) {
            log.warn("msgType [{}] is invalid", msgType);
            return;
        }

        log.info("msgType is [{}]", msgType);

        MessageHandler messageHandler = messageHandlerMap.get(msgType);
        messageHandler.handle(channelHandlerContext, transportMessage);

    }

    public static String toLowerCaseFirstOne(String s) {

        if (Character.isLowerCase(s.charAt(0))) {
            return s;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(Character.toLowerCase(s.charAt(0)));
        sb.append(s.substring(1));

        return sb.toString();

    }

}
