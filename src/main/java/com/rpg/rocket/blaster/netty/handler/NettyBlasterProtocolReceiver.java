package com.rpg.rocket.blaster.netty.handler;

import com.rpg.rocket.blaster.message.MessageDispatcher;
import com.rpg.rocket.blaster.protocol.BlasterProtocol;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: liubin
 * Date: 14-2-6
 */
@ChannelHandler.Sharable
public class NettyBlasterProtocolReceiver extends ChannelInboundHandlerAdapter {

    private static final Logger log = LoggerFactory.getLogger(NettyBlasterProtocolReceiver.class);

    MessageDispatcher messageDispatcher = MessageDispatcher.getInstance();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        messageDispatcher.receive(ctx.channel(), (BlasterProtocol) msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
