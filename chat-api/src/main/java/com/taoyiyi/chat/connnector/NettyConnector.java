package com.taoyiyi.chat.connnector;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class NettyConnector {


    /**
     * 通道容器
     * <p>
     * key:deviceId
     * value:ChannelHandlerContext
     */
    private static final Map<String, ChannelHandlerContext> DEVICE_CODE_CHANNEL_HANDLER_CONTEXT_MAP = new ConcurrentHashMap<>(1000);


    public synchronized void remove(ChannelHandlerContext channelHandlerContext) {

        try {


            AttributeKey<String> key = AttributeKey.valueOf("userCode");

            Attribute<String> attr = channelHandlerContext.channel().attr(key);
            String userCode = attr.get();
            if (!StringUtils.isEmpty(userCode)) {

                USER_CODE_CHANNEL_HANDLER_CONTEXT_MAP.remove(userCode);
            }
            log.warn(" remove netty connect, userCode:{},nettyId:{}", userCode,getNettyId(channelHandlerContext));

        } catch (Exception e) {
            log.error("removeDeviceChannel error", e);
        } finally {
            channelHandlerContext.close();
        }

    }

    public String getUserCode(ChannelHandlerContext channelHandlerContext){
        AttributeKey<String> key = AttributeKey.valueOf("userCode");
        Attribute<String> attr = channelHandlerContext.channel().attr(key);
       return attr.get();
    }

    /**
     * 通道容器
     * <p>
     * <p>
     * value:ChannelHandlerContext
     */
    private static final Map<String, ChannelHandlerContext> USER_CODE_CHANNEL_HANDLER_CONTEXT_MAP = new ConcurrentHashMap<>();



    public  void saveUserChannel(ChannelHandlerContext channelHandlerContext, String userCode) {

        if (StringUtils.isEmpty(userCode)) {
            return;
        }
        AttributeKey<String> key = AttributeKey.valueOf("userCode");

        channelHandlerContext.channel().attr(key).set(userCode);
        USER_CODE_CHANNEL_HANDLER_CONTEXT_MAP.put(userCode, channelHandlerContext);

    }


    public  ChannelHandlerContext getDeviceChannelByDeviceId(String deviceId) {
        return DEVICE_CODE_CHANNEL_HANDLER_CONTEXT_MAP.get(deviceId);
    }

    public  ChannelHandlerContext getUserChannelContextByUserCode(String userCode) {
        ChannelHandlerContext channelHandlerContext = USER_CODE_CHANNEL_HANDLER_CONTEXT_MAP.get(userCode);
        return channelHandlerContext;
    }

    public  String getDeviceIdByNettyId(String nettyId) {
        return getKeyInChannelHandlerContextMap(DEVICE_CODE_CHANNEL_HANDLER_CONTEXT_MAP, nettyId);
    }

    public  String getUserIdByNettyId(String nettyId) {
        return getKeyInChannelHandlerContextMap(USER_CODE_CHANNEL_HANDLER_CONTEXT_MAP, nettyId);
    }

    public  String getNettyId(ChannelHandlerContext channelHandlerContext) {
        return channelHandlerContext.channel().id().asShortText();
    }

    private  String getKeyInChannelHandlerContextMap(Map<String, ChannelHandlerContext> channelHandlerContextMap, String value) {

        if (StringUtils.isEmpty(value)) {
            return null;
        }

        Set<Map.Entry<String, ChannelHandlerContext>> entrySet = channelHandlerContextMap.entrySet();
        if (CollectionUtils.isEmpty(entrySet)) {
            return null;
        }

        for (Map.Entry<String, ChannelHandlerContext> entry : entrySet) {

            String nettyId = getNettyId(entry.getValue());

            if (!StringUtils.equals(value, nettyId)) {
                continue;
            }

            return entry.getKey();

        }

        return null;

    }

}
