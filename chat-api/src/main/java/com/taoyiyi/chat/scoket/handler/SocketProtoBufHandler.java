package com.taoyiyi.chat.scoket.handler;


import com.taoyiyi.chat.connnector.NettyConnector;
import com.taoyiyi.chat.processor.SocketMessageProcessor;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

@Component
@Sharable
@Slf4j
public class SocketProtoBufHandler extends ChannelInboundHandlerAdapter {

    @Resource
    private NettyConnector nettyConnector;

    @Resource
    private SocketMessageProcessor socketMessageProcessor;



    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext) {

        String nettyId = nettyConnector.getNettyId(channelHandlerContext);
        String deviceId = nettyConnector.getDeviceIdByNettyId(nettyId);
        String userId = nettyConnector.getUserIdByNettyId(nettyId);
        log.info("channelActive, nettyId is [{}], deviceId is [{}], userId is [{}]", nettyId, deviceId, userId);

    }

    @Override
    public void channelInactive(ChannelHandlerContext channelHandlerContext) {
        log.info("channelInactive , channelHandlerContext: {}",channelHandlerContext.channel().id().asShortText());

        exit(channelHandlerContext);

        nettyConnector.remove(channelHandlerContext);

    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object message) {
        log.info("channelRead , channelHandlerContext: {}, message: {}",channelHandlerContext,message);

        socketMessageProcessor.handler(channelHandlerContext, message); // 需要放到单独的分发器中处理
        channelHandlerContext.flush();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext channelHandlerContext) {
        channelHandlerContext.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable) {

        exit(channelHandlerContext);

        nettyConnector.remove(channelHandlerContext);

    }


    @Override
    public void handlerRemoved(ChannelHandlerContext channelHandlerContext) {
        log.info("handlerRemoved , channelHandlerContext: {}",channelHandlerContext.channel().id().asShortText());

        exit(channelHandlerContext);

        nettyConnector.remove(channelHandlerContext);

    }

    private void exit(ChannelHandlerContext channelHandlerContext) {

        try {

            if (Objects.isNull(channelHandlerContext)) return;

            String nettyId = nettyConnector.getNettyId(channelHandlerContext);
            if (StringUtils.isEmpty(nettyId)) return;

            String deviceId = nettyConnector.getDeviceIdByNettyId(nettyId);
            if (StringUtils.isEmpty(deviceId)) return;

            // 模拟设备下线
//            ChatOfflineNotice.ChatOfflineNoticeMessage contentMessage = ChatOfflineNotice.ChatOfflineNoticeMessage.newBuilder().setChatId(deviceId).build();
//            TransportMessageOuterClass.TransportMessage message = TransportMessageOuterClass.TransportMessage.newBuilder().setId(RandomUtil.generate()).setAccessToken(nettyId).setMsgType(TransportMessageOuterClass.EnumMsgType.ChatOfflineNotice).setContent(Any.pack(contentMessage)).build();
//
//            ChatOfflineNoticeHandler.handle(channelHandlerContext, message);

        } catch (Exception e) {
            log.error("error is", e);
        }

    }

}
