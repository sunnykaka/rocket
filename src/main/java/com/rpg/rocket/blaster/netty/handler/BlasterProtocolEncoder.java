package com.rpg.rocket.blaster.netty.handler;

import com.rpg.rocket.blaster.protocol.BlasterProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;


public class BlasterProtocolEncoder extends MessageToByteEncoder<BlasterProtocol> {

    @Override
    protected void encode(ChannelHandlerContext ctx, BlasterProtocol msg, ByteBuf out) throws Exception {
        msg.encode(out);
    }
}