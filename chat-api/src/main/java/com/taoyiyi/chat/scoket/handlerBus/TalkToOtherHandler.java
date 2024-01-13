package com.taoyiyi.chat.scoket.handlerBus;


import com.taoyiyi.chat.constant.RespCodeEnum;
import com.taoyiyi.chat.proto.TalkToOther;
import com.taoyiyi.chat.proto.TransportMessageOuterClass;
import com.taoyiyi.chat.pusher.BasePusher;
import com.taoyiyi.chat.pusher.ChannelPusher;
import com.taoyiyi.chat.scoket.handler.MessageHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

@Component
@Slf4j
public class TalkToOtherHandler implements MessageHandler {

    @Resource
    private BasePusher basePusher;

    @Resource
    private ChannelPusher channelPusher;


    /**
     * 设备校验(通过存储token和imei建立连接)
     *
     * @param channelHandlerContext
     * @param transportMessage
     */
    @Override
    public void handle(ChannelHandlerContext channelHandlerContext, TransportMessageOuterClass.TransportMessage transportMessage) {

        try {

            TalkToOther.TalkToOtherMessage req = transportMessage.getContent().unpack(TalkToOther.TalkToOtherMessage.class);
            if (Objects.isNull(req)) {
                basePusher.sendErrorMessage(channelHandlerContext, TransportMessageOuterClass.EnumErrorCode.InvalidParam, transportMessage.getId(), RespCodeEnum.ERROR_PARAM.getMemo());
                return;
            }
            channelPusher.push(channelHandlerContext, req.getToUser(), TransportMessageOuterClass.EnumMsgType.TalkToOther, transportMessage, req);

        } catch (Exception e) {
            log.error("handle error", e);
            basePusher.sendErrorMessage(channelHandlerContext, TransportMessageOuterClass.EnumErrorCode.InvalidParam, transportMessage.getId(), RespCodeEnum.FAILE.getMemo());
        }

    }


}