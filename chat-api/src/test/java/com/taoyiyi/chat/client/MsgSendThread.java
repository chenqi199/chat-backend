package com.taoyiyi.chat.client;


import com.google.protobuf.Any;
import com.google.protobuf.Message;
import com.taoyiyi.chat.proto.TransportMessageOuterClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
public class MsgSendThread {
    private static Logger logger = LogManager.getLogger(ChatClient.class);
    private final List<ProtobufMsg> sendMsgList;
    private NettyClient client;
    private long msgId;
    private String accessToken;
    private volatile boolean isRunning = false;
    private SendTread sender;
    private final Object mLocked = new Object();

    public MsgSendThread(NettyClient client) {
        this.client = client;
        this.msgId = 0L;
        this.accessToken = null;
        this.sendMsgList = new ArrayList<>();
    }

    public static final String URL_PREFIX = "TYY";

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        if (accessToken == null) {
            return;
        }
        synchronized (this.mLocked) {
            this.mLocked.notify();
        }
    }

    public long addMsgToSendList(TransportMessageOuterClass.EnumMsgType msgType, Message content) {
        return addMsgToSendList(msgType, content, false);
    }

    public long addMsgToSendList(TransportMessageOuterClass.EnumMsgType msgType, Message content, boolean insert) {
        log.info("addMsgToSendList , msgType: {}, content: {}, insert: {}", msgType, content, insert);

        ProtobufMsg msg = new ProtobufMsg(msgType, content, this.msgId++);
        synchronized (this.mLocked) {
            if (insert) {
                this.sendMsgList.add(0, msg);
            } else {
                this.sendMsgList.add(msg);
            }
            this.mLocked.notify();
        }
        return this.msgId - 1L;
    }

    public boolean sendMsgSync(TransportMessageOuterClass.EnumMsgType msgType, Message content) {

        if (this.client.isConnect()) {
            TransportMessageOuterClass.TransportMessage.Builder builder = TransportMessageOuterClass.TransportMessage.newBuilder()
                    .setMsgType(msgType).setId(this.msgId++).setAccessToken(this.accessToken);
            if (content != null) {
                builder.setContent(Any.pack(content, "TYY"));
            }
            int ret = this.client.sendMsg(builder.build());
            System.out.println("SyncMsg " + msgType + " " + this.msgId);
            return (ret > 0);
        }
        return false;
    }

    public boolean sendMsgSync(TransportMessageOuterClass.EnumMsgType msgType, Message content, boolean idReset) {
        if (this.client.isConnect()) {
            if (idReset) {
                this.msgId = 0L;
            }
            TransportMessageOuterClass.TransportMessage.Builder builder = TransportMessageOuterClass.TransportMessage.newBuilder().setMsgType(msgType).setId(this.msgId++);
            if (content != null) {
                builder.setContent(Any.pack(content, "TYY"));
            }
            int ret = this.client.sendMsg(builder.build());
            return (ret > 0);
        }else {
            // todo 重连
        }
        return false;
    }

    public boolean isAccredit() {
        return true;
    }

    private int sendMsg(ProtobufMsg message) {
        TransportMessageOuterClass.TransportMessage.Builder builder = TransportMessageOuterClass.TransportMessage.newBuilder();
        builder.setMsgType(message.msgType)
                .setAccessToken(this.accessToken)
                .setId(message.msgId);
        if (message.content != null) {
            builder.setContent(Any.pack(message.content, "TYY"));
        }
        TransportMessageOuterClass.TransportMessage req = builder.build();
        int ret = this.client.sendMsg(req);
        return ret;
    }

    public void startRun() {
        if (!this.isRunning) {
            this.sender = new SendTread();
            this.sender.start();
        } else {
            logger.error("Sender alread run");
        }
    }

    public void stopRun() {
        synchronized (this.mLocked) {
            this.accessToken = null;
            if (this.sender != null) {
                this.sender.interrupt();
            }
            this.mLocked.notify();
        }
        this.sender = null;
    }

    class SendTread
            extends Thread {
        public void run() {
            MsgSendThread.logger.error("Sender start run");
            MsgSendThread.this.isRunning = true;
//            ChatClient.get().regChannel(TestLocalMsgConstant.fromUser);// u2 注册一个连接通道
//            ChatClient.get().regChannel("U18ce81029b4XslwF8B5f");// u1 注册一个连接通道

        }
    }
}
