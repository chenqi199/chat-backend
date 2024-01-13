package com.taoyiyi.chat.pusher;


import com.google.protobuf.Any;
import com.google.protobuf.Message;
import com.taoyiyi.chat.proto.ErrorMessageOuterClass;
import com.taoyiyi.chat.proto.TransportMessageOuterClass;
import com.taoyiyi.chat.util.RandomUtil;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Slf4j
public class BasePusher {

    public void sendMessage(ChannelHandlerContext channelHandlerContext, TransportMessageOuterClass.EnumMsgType msgType, Long refMsgId, Message message) {

        Message msg = getMessage(msgType, refMsgId, message);
        channelHandlerContext.channel().writeAndFlush(msg);

    }

    public void sendErrorMessage(ChannelHandlerContext channelHandlerContext, TransportMessageOuterClass.EnumErrorCode errorCode, Long refMsgId, String errorMessage) {
        sendMessage(channelHandlerContext, TransportMessageOuterClass.EnumMsgType.Error, null, getErrorMessage(errorCode, errorMessage));
    }


    private Message getMessage(TransportMessageOuterClass.EnumMsgType type, Long refMsgId, Message resp) {

        TransportMessageOuterClass.TransportMessage.Builder builder = TransportMessageOuterClass.TransportMessage.newBuilder();

        builder.setId(RandomUtil.generate(BigDecimal.ZERO.intValue(), Integer.MAX_VALUE));


        if (null != type) {
            builder.setMsgType(type);
        }
        if (null != refMsgId) {
            builder.setRefMessageId(refMsgId);
        }
        if (null != resp) {
            builder.setContent(Any.pack(resp));
        }

        return builder.build();

    }

    private ErrorMessageOuterClass.ErrorMessage getErrorMessage(TransportMessageOuterClass.EnumErrorCode errorCode, String errorMsg) {
        return ErrorMessageOuterClass.ErrorMessage.newBuilder().setErrorCode(errorCode).setErrorMsg(errorMsg).build();
    }

}
