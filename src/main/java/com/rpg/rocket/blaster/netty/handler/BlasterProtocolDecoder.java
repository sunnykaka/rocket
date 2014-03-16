package com.rpg.rocket.blaster.netty.handler;

import com.rpg.rocket.blaster.protocol.BlasterProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;


public class BlasterProtocolDecoder extends ByteToMessageDecoder{

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        BlasterProtocol protocol = BlasterProtocol.decode(in);
        if(protocol == null) return;
        out.add(protocol);
    }
}