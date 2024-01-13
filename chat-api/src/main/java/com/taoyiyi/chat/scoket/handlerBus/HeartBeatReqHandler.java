package com.taoyiyi.chat.scoket.handlerBus;


import com.taoyiyi.chat.connnector.NettyConnector;
import com.taoyiyi.chat.proto.TransportMessageOuterClass;
import com.taoyiyi.chat.pusher.BasePusher;
import com.taoyiyi.chat.scoket.handler.MessageHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

@Component
@Slf4j
public class HeartBeatReqHandler implements MessageHandler {


    @Resource
    private NettyConnector nettyConnector;
    @Resource
    private BasePusher basePusher;


    /**
     * 心跳
     *
     * @param channelHandlerContext
     * @param transportMessage
     */
    @Override
    public void handle(ChannelHandlerContext channelHandlerContext, TransportMessageOuterClass.TransportMessage transportMessage) {

        try {

            String nettyId = nettyConnector.getNettyId(channelHandlerContext);

            log.info("HeartBeatReqHandler , userCode:{}, nettyId: {}", nettyConnector.getUserCode(channelHandlerContext), nettyId);

            basePusher.sendMessage(channelHandlerContext, TransportMessageOuterClass.EnumMsgType.MsgReceivedAck, transportMessage.getId(), null);

        } catch (Exception e) {
            log.error("handle error", e);
        }

    }

}