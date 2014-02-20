package com.rpg.rocket.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.CharsetUtil;


public class RocketProtocolEncoder extends MessageToByteEncoder<RocketProtocol> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RocketProtocol msg, ByteBuf out) throws Exception {
        msg.encode(out);
    }
}