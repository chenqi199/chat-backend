package com.taoyiyi.chat.util;


import com.google.protobuf.MessageLite;
import com.taoyiyi.chat.proto.TransportMessageOuterClass;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class DecoderUtil extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

        // 可读长度小于包头长度退出
        while (byteBuf.readableBytes() > 4) {

            byteBuf.markReaderIndex();

            // 获取包头中的body长度
            byte b3 = byteBuf.readByte();
            byte b2 = byteBuf.readByte();
            byte b1 = byteBuf.readByte();
            byte b0 = byteBuf.readByte();

            int length = (b0 & 0xff) | ((b1 << 8) & 0xff00) | ((b2 << 16) & 0xff0000) | ((b3 << 24) & 0xff000000);

            // 可读长度小于body长度恢复读指针
            if (byteBuf.readableBytes() < length) {
                byteBuf.resetReaderIndex();
                return;
            }

            // 读取body
            ByteBuf bodyByteBuf = byteBuf.readBytes(length);

            byte[] array;
            int offset;

            int readableLen = bodyByteBuf.readableBytes();
            if (bodyByteBuf.hasArray()) {
                array = bodyByteBuf.array();
                offset = bodyByteBuf.arrayOffset() + bodyByteBuf.readerIndex();
            } else {
                array = new byte[readableLen];
                bodyByteBuf.getBytes(bodyByteBuf.readerIndex(), array, 0, readableLen);
                offset = 0;
            }

            // 反序列化
            MessageLite result = decodeBody(array, offset, readableLen);
            list.add(result);

            // 释放
            bodyByteBuf.release();

        }
    }

    public MessageLite decodeBody(byte[] bytes, int offset, int length) throws Exception {
        return TransportMessageOuterClass.TransportMessage.getDefaultInstance().getParserForType().parseFrom(bytes, offset, length);
    }

}
