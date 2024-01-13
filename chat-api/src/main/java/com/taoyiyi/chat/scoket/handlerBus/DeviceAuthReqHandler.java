package com.taoyiyi.chat.scoket.handlerBus;


import com.taoyiyi.chat.connnector.NettyConnector;
import com.taoyiyi.chat.constant.RespCodeEnum;
import com.taoyiyi.chat.proto.DeviceAuthReq;
import com.taoyiyi.chat.proto.DeviceAuthRsp;
import com.taoyiyi.chat.proto.TransportMessageOuterClass;
import com.taoyiyi.chat.pusher.BasePusher;
import com.taoyiyi.chat.scoket.handler.MessageHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

@Component
@Slf4j
public class DeviceAuthReqHandler implements MessageHandler {

    @Resource
    private NettyConnector nettyConnector;

    @Resource
    private BasePusher basePusher;


    /**
     * 设备校验(通过存储token和imei建立连接)
     *
     * @param channelHandlerContext
     * @param transportMessage
     */
    @Override
    public void handle(ChannelHandlerContext channelHandlerContext, TransportMessageOuterClass.TransportMessage transportMessage) {

        try {

            DeviceAuthReq.DeviceAuthReqMessage req = transportMessage.getContent().unpack(DeviceAuthReq.DeviceAuthReqMessage.class);
            if (Objects.isNull(req)) {
                basePusher.sendErrorMessage(channelHandlerContext, TransportMessageOuterClass.EnumErrorCode.InvalidParam, transportMessage.getId(), RespCodeEnum.ERROR_PARAM.getMemo());
                return;
            }

            if (!DeviceAuthReq.DeviceAuthReqMessage.EnumAuthType.UserCode.equals(req.getAuthType())) {
                basePusher.sendErrorMessage(channelHandlerContext, TransportMessageOuterClass.EnumErrorCode.InvalidParam, transportMessage.getId(), RespCodeEnum.ERROR_PARAM.getMemo());
                return;
            }

            if (StringUtils.isEmpty(req.getCredential())) {
                basePusher.sendErrorMessage(channelHandlerContext, TransportMessageOuterClass.EnumErrorCode.InvalidParam, transportMessage.getId(), RespCodeEnum.ERROR_PARAM.getMemo());
                return;
            }


            // 存储设备ID和通道信息
            nettyConnector.saveUserChannel(channelHandlerContext, req.getCredential());

            DeviceAuthRsp.DeviceAuthRspMessage.ExtraMessage extraMessage =
                    DeviceAuthRsp.DeviceAuthRspMessage.ExtraMessage.newBuilder()
                            .setSupplierId(1).setSupplierName("淘伊伊聊天系统")
                            .build();
            DeviceAuthRsp.DeviceAuthRspMessage deviceAuthRspMessage = DeviceAuthRsp.DeviceAuthRspMessage.newBuilder()
                    .setAccessToken(channelHandlerContext.channel().id().asShortText()).setExtra(extraMessage).build();

            basePusher.sendMessage(channelHandlerContext, TransportMessageOuterClass.EnumMsgType.DeviceAuthRsp, transportMessage.getId(), deviceAuthRspMessage);

        } catch (Exception e) {
            log.error("handle error", e);
            basePusher.sendErrorMessage(channelHandlerContext, TransportMessageOuterClass.EnumErrorCode.InvalidParam, transportMessage.getId(), RespCodeEnum.FAILE.getMemo());
        }

    }


}