package com.rpg.rocket.client;

import com.rpg.rocket.blaster.protocol.BlasterProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class RocketClientProtocolHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        BlasterProtocol protocol = (BlasterProtocol) msg;
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