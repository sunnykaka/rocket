package com.rpg.rocket.server;

import com.rpg.rocket.blaster.registry.MessageHandlerRegistry;
import com.rpg.rocket.pb.DescriptorRegistry;
import com.rpg.rocket.protocol.RocketProtocolDecoder;
import com.rpg.rocket.protocol.RocketProtocolEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: liubin
 * Date: 14-2-6
 */
public class RocketServer {

    private static final Logger log = LoggerFactory.getLogger(RocketServer.class);

    private int port;

    public RocketServer(int port) {
        this.port = port;

        init();
    }

    public Channel accept(final ServerBootstrap b) throws InterruptedException{
        Channel channel = b.bind(port).sync().channel();

        channel.closeFuture().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                log.info("server closed.");
                b.childGroup().shutdownGracefully();
                b.group().shutdownGracefully();
            }
        });

        return channel;
    }

    public Channel accept(final ChannelInboundHandlerAdapter handler) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new RocketProtocolDecoder());
                        ch.pipeline().addLast(new RocketProtocolEncoder());
                        if(handler != null) {
                            ch.pipeline().addLast(handler);
                        }
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        return accept(b);
    }

    public Channel accept() throws InterruptedException {
        return accept(new RocketServerProtocolHandler());
    }

    public static void main(String[] args) throws Exception {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 8080;
        }
        RocketServer rocketServer = new RocketServer(port);

        rocketServer.accept();
    }

    private void init() {
        DescriptorRegistry.getInstance().init();
        MessageHandlerRegistry.getInstance().init();
    }

}
