package com.rpg.rocket.client;

import com.rpg.rocket.protocol.RocketProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

public class RocketClientProtocolHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        RocketProtocol protocol = (RocketProtocol) msg;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("actived");
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}