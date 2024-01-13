package com.taoyiyi.chat.scoket.handler;


import com.taoyiyi.chat.proto.TransportMessageOuterClass;
import io.netty.channel.ChannelHandlerContext;

public interface MessageHandler {

    void handle(ChannelHandlerContext channelHandlerContext, TransportMessageOuterClass.TransportMessage transportMessage);

}
