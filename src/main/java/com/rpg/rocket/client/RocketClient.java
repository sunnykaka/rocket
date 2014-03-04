package com.rpg.rocket.client;

import com.rpg.rocket.blaster.registry.MessageHandlerRegistry;
import com.rpg.rocket.pb.DescriptorRegistry;
import com.rpg.rocket.protocol.RocketProtocolDecoder;
import com.rpg.rocket.protocol.RocketProtocolEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RocketClient {

    private static final Logger log = LoggerFactory.getLogger(RocketClient.class);


    public RocketClient() {
        init();
    }

    private void init() {
        DescriptorRegistry.getInstance().init();
        MessageHandlerRegistry.getInstance().init();
    }

    public static void main(String[] args) throws Exception {
//        String host = args[0];
//        int port = Integer.parseInt(args[1]);
        String host = "localhost";
        int port = 8080;

        RocketClient rocketClient = new RocketClient();
        rocketClient.connect(host, port);
    }

    public Channel connect(String host, int port, final Bootstrap b) throws InterruptedException {
        // Start the client.
        Channel channel = b.connect(host, port).sync().channel();
        channel.closeFuture().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                log.info("client closed.");
                b.group().shutdownGracefully();
            }
        });

        return channel;
    }

    public Channel connect(String host, int port, final ChannelInboundHandlerAdapter handler) throws InterruptedException {
        final EventLoopGroup workerGroup = new NioEventLoopGroup();

        Bootstrap b = new Bootstrap();
        b.group(workerGroup);
        b.channel(NioSocketChannel.class);
        b.option(ChannelOption.SO_KEEPALIVE, true);
        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new RocketProtocolDecoder());
                ch.pipeline().addLast(new RocketProtocolEncoder());
                if(handler != null) {
                    ch.pipeline().addLast(handler);
                }
            }
        });

        return connect(host, port, b);
    }

    public Channel connect(String host, int port) throws InterruptedException {
        return connect(host, port, new RocketClientProtocolHandler());
    }


}