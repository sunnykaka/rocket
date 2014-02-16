package com.rpg.rocket.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.io.UnsupportedEncodingException;

public class RocketClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf m = (ByteBuf) msg;
        try {
            System.out.println(m.toString(CharsetUtil.UTF_8));
            ctx.close();
        } finally {
            m.release();
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("actived");
//        ByteBuf firstMessage = Unpooled.buffer(64);
//        try {
//            firstMessage.writeBytes("你好啊".getBytes("UTF-8"));
//            ctx.writeAndFlush(firstMessage);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
    }

//    @Override
//    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        ctx.flush();
//    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}