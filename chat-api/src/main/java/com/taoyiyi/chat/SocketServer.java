package com.taoyiyi.chat;


import com.taoyiyi.chat.scoket.handler.SocketProtoBufHandler;
import com.taoyiyi.chat.util.DecoderUtil;
import com.taoyiyi.chat.util.EncoderUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Component
@Slf4j
public class SocketServer {

    @Value("${server.socket-port:19888}")
    private Integer port;

    @Resource
    private SocketProtoBufHandler socketProtoBufHandler;

    @PostConstruct
    public void socketServer() {

        new Thread(() -> {

            // 1.创建两个线程组
            // 一个用于处理服务器端接收客户端连接
            // 一个进行网络通信(网络读写)
            EventLoopGroup bossGroup = new NioEventLoopGroup();
            EventLoopGroup workerGroup = new NioEventLoopGroup();

            try {

                // 2.创建辅助工具类,用于服务器通道的一系列配置
                ServerBootstrap serverBootstrap = new ServerBootstrap();
                serverBootstrap.group(bossGroup, workerGroup);
                serverBootstrap.channel(NioServerSocketChannel.class);                  // 指定NIO的模式
                serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024);           // 设置tcp缓冲区
                serverBootstrap.childOption(ChannelOption.SO_SNDBUF, 32 * 1024);       // 设置发送缓冲大小
                serverBootstrap.option(ChannelOption.SO_RCVBUF, 32 * 1024);       // 这是接收缓冲大小
                serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);         // 保持连接
                serverBootstrap.childOption(ChannelOption.TCP_NODELAY, true);          // 禁用Nagle算法
                serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel socketChannel) {

                        socketChannel.pipeline().addLast(new DecoderUtil());
                        socketChannel.pipeline().addLast(new EncoderUtil());
                        socketChannel.pipeline().addLast(socketProtoBufHandler);
                    }
                });

                // 3.绑定端口
                ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
                log.info("socket start success, post is [{}]", port);

                // 4.等待服务端监听端口关闭
                channelFuture.channel().closeFuture().sync();

            } catch (Exception e) {
                log.error("socket start fail", e);
            } finally {
                workerGroup.shutdownGracefully();
                bossGroup.shutdownGracefully();
            }

        }).start();

    }

}
