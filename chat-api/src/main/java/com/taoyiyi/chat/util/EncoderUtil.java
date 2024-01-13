package com.taoyiyi.chat.util;

import com.google.protobuf.MessageLite;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class EncoderUtil extends MessageToByteEncoder<MessageLite> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, MessageLite messageLite, ByteBuf byteBuf) {

        byte[] body = messageLite.toByteArray();
        byte[] header = encodeHeader(body.length);

        byteBuf.writeBytes(header);
        byteBuf.writeBytes(body);

    }

    private byte[] encodeHeader(int value) {

        byte[] bytes = new byte[4];
        bytes[0] = (byte) ((value >> 24) & 0xFF);
        bytes[1] = (byte) ((value >> 16) & 0xFF);
        bytes[2] = (byte) ((value >> 8) & 0xFF);
        bytes[3] = (byte) (value & 0xFF);

        return bytes;

    }

}
