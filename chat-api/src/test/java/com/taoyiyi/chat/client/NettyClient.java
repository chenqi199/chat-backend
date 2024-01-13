package com.taoyiyi.chat.client;


import com.google.protobuf.Any;
import com.taoyiyi.chat.proto.TransportMessageOuterClass;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

@Slf4j
public class NettyClient {

    private static String host = "192.168.1.9";
    private static int port = 19888;
    private static final int RECONNECT_TIME = 5;
    private static NettyClient client;
    private ChannelHandlerContext channel;
    private MsgSendThread sendThread;
    private volatile Bootstrap mB;
    private volatile boolean isClosed;
    private volatile boolean isConneting;

    private String userCode;
    private volatile EventLoopGroup workerGroup;

    public static NettyClient getClient() {
        if (client == null) {
            synchronized (NettyClient.class) {
                if (client == null) {
                    client = new NettyClient();
                }
            }
        }
        return client;
    }

    private NettyClient() {
        this.sendThread = new MsgSendThread(this);
        initBootstrap();
    }

    public MsgSendThread getMsgSender() {
        return this.sendThread;
    }

    public static void setHost(String host, int port, boolean isSave) {
        NettyClient.host = host;
        if (port != 0) {
            NettyClient.port = port;
        }
    }

    public NettyClient setUserCode(String userCode) {
        this.userCode = userCode;
        return this;
    }



    private void initBootstrap() {
        this.workerGroup = new NioEventLoopGroup();
        this.mB = new Bootstrap();
        this.mB.group(this.workerGroup).channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, Boolean.TRUE)
                .option(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                .option(ChannelOption.SO_SNDBUF, 32 * 1024)  // 设置发送缓冲大小
                .option(ChannelOption.SO_RCVBUF, 32 * 1024)       // 这是接收缓冲大小
                .handler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new CustomProtobufDecoder()).addLast(new CustomProtobufEncoder())
                                .addLast(new NettyClientHandler());
                    }
                }).option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);
    }

    public void connect() {
        (new Thread() {
            public void run() {
                NettyClient.this.connectServer();
            }
        }).start();
    }

    public void connectServer() {
        try {
            connect(port, host);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        this.isClosed = true;
        if (this.channel != null) {
            this.channel.close();
            this.channel = null;
        }
    }

    public void connect(int port, String host) throws Exception {
        synchronized (this) {
            if (this.channel != null && !this.channel.isRemoved()) {
                return;
            }
        }
        if (this.isConneting) {
            return;
        }
        this.isConneting = true;
        this.isClosed = false;
        try {
            ChannelFuture f = this.mB.connect(host, port).sync();
            f.addListener((ChannelFutureListener) future -> {
                if (future.isDone()) {
                    NettyClient.this.isConneting = false;
                    if (future.isSuccess()) {
                        log.info("netty_connect: success");
                        ChannelPipeline pipeline = future.channel().pipeline();
                        ChannelHandlerContext ctx = pipeline.lastContext();
                        this.channel = ctx;
                        this.sendThread.startRun();

                        ChatClient.get().regChannel(userCode);
                    } else {
                        future.channel().eventLoop().schedule(new Runnable() {
                            public void run() {
                                NettyClient.this.connectServer();
                            }
                        }, 5L, TimeUnit.SECONDS);
                    }
                }
            });
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            System.out.println(e.toString());
            TimeUnit.SECONDS.sleep(5L);
            this.isConneting = false;
            connect();
        } finally {
        }
    }

    public boolean isConnect() {
        return (this.channel != null);
    }

    public int sendMsg(byte[] bytes) {
        ByteBuffer writeBuffer = ByteBuffer.wrap(bytes);
        if (this.channel != null) {
            this.channel.writeAndFlush(writeBuffer);
            return bytes.length;
        }
        return 0;
    }

    public int sendMsg(Object request) {
        log.info(". , request: {}", request);

        if (this.channel != null) {
            this.channel.writeAndFlush(request);
            return 1;
        }
        return 0;
    }

    public class NettyClientHandler<T> extends ChannelInboundHandlerAdapter {
        public void channelActive(ChannelHandlerContext ctx) {
            synchronized (NettyClient.client) {
                if (NettyClient.this.channel == null) {
                    NettyClient.this.channel = ctx;
                    NettyClient.this.sendThread.startRun();
                }
                NettyClient.this.isConneting = false;
            }
            log.error("通道-开启：" + ctx.channel().localAddress() + " " + ctx.channel().remoteAddress());
            ctx.fireChannelActive();
        }

        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            boolean reConn = false;
            synchronized (NettyClient.client) {
                if (ctx.equals(NettyClient.this.channel)) {
                    NettyClient.this.channel = null;
                    NettyClient.this.sendThread.stopRun();
                    if (!NettyClient.this.isClosed) {
                        reConn = true;
                        ctx.channel().eventLoop().schedule(new Runnable() {
                            public void run() {
                                NettyClient.this.connectServer();
                            }
                        }, 5L, TimeUnit.SECONDS);
                    }
                    NettyClient.this.isConneting = false;
                }
            }
          super.channelInactive(ctx);
            log.error("通道-关闭, 重连：" + reConn);
        }



        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            log.error("链接异常：" + cause.getMessage());
            cause.printStackTrace();

            super.exceptionCaught(ctx, cause);
        }

        public Map<TransportMessageOuterClass.EnumMsgType, BiConsumer<ChannelHandlerContext, Class<T>>> consumerMap = new HashMap<>();


        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            log.info("channelRead , ctx: {}, msg: {}", ctx, msg);

            TransportMessageOuterClass.TransportMessage transportMessage = (TransportMessageOuterClass.TransportMessage) msg;
            Any any = transportMessage.getContent();
            TransportMessageOuterClass.EnumMsgType msgType = transportMessage.getMsgType();
            ReceiveHandle.getInstance().handleMsgType(any, msgType);
            ctx.fireChannelRead(msg);
        }

        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
                IdleStateEvent event = (IdleStateEvent) evt;
                if (event.state() != IdleState.READER_IDLE)
                    if (event.state() == IdleState.WRITER_IDLE) {
                        log.error("ChannelIdle：" + ctx.channel().localAddress());
                    } else if (event.state() == IdleState.ALL_IDLE) {
                    }
            }
        }
    }
}
