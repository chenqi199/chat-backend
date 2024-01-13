package com.taoyiyi.chat.pusher;


import com.google.protobuf.Message;

import com.taoyiyi.chat.connnector.NettyConnector;
import com.taoyiyi.chat.proto.TransportMessageOuterClass;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

@Component
@Slf4j
public class ChannelPusher {

    @Resource
    private NettyConnector nettyConnector;

    @Resource
    private BasePusher basePusher;



    @Async("async1")
    public void push(ChannelHandlerContext channelHandlerContext, String userCode, TransportMessageOuterClass.EnumMsgType type, TransportMessageOuterClass.TransportMessage transportMessage, Message message) {

        ChannelHandlerContext chc = nettyConnector.getUserChannelContextByUserCode(userCode);
        if (Objects.isNull(chc)) {
            //  todo 通知下线
            log.info("find user offline :{}",userCode);


            return;
        }
        // 转发给手机端
        basePusher.sendMessage(chc, type,  null, message);

    }



}
