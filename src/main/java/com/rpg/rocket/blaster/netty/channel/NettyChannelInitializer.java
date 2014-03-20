package com.rpg.rocket.blaster.netty.channel;

import com.rpg.rocket.blaster.netty.handler.BlasterProtocolDecoder;
import com.rpg.rocket.blaster.netty.handler.BlasterProtocolEncoder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.EventExecutorGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * User: liubin
 * Date: 14-3-20
 */
public class NettyChannelInitializer extends ChannelInitializer<SocketChannel> {

    private EventExecutorGroup eventExecutorGroup;
    private ChannelHandler channelHandler;

    public NettyChannelInitializer(EventExecutorGroup eventExecutorGroup, ChannelHandler channelHandler) {
        this.eventExecutorGroup = eventExecutorGroup;
        this.channelHandler = channelHandler;
    }

    public NettyChannelInitializer(ChannelHandler channelHandler) {
        this(null, channelHandler);
    }

    public NettyChannelInitializer() {
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new BlasterProtocolDecoder());
        ch.pipeline().addLast(new BlasterProtocolEncoder());
        if(channelHandler != null) {
            if(eventExecutorGroup != null) {
                ch.pipeline().addLast(eventExecutorGroup, channelHandler);
            } else {
                ch.pipeline().addLast(channelHandler);
            }
        }
    }
}
