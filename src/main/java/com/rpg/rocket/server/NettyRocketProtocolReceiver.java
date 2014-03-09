package com.rpg.rocket.server;

import com.rpg.rocket.blaster.BlasterReceiver;
import com.rpg.rocket.blaster.BlasterSender;
import com.rpg.rocket.common.SysConstants;
import com.rpg.rocket.exception.RocketProtocolException;
import com.rpg.rocket.protocol.ResponseWrapper;
import com.rpg.rocket.protocol.RocketProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: liubin
 * Date: 14-2-6
 */
public class NettyRocketProtocolReceiver extends ChannelInboundHandlerAdapter {

    private static final Logger log = LoggerFactory.getLogger(NettyRocketProtocolReceiver.class);

    BlasterReceiver blasterReceiver = new BlasterReceiver();
    BlasterSender blasterSender = new BlasterSender();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        RocketProtocol protocol = (RocketProtocol) msg;
        ResponseWrapper response = null;
        try {
            response = blasterReceiver.receive(protocol);
        } catch (RocketProtocolException e) {
            if(RocketProtocol.Type.REQUEST.equals(protocol.getType())) {
                RocketProtocol.Status status = e.getStatus();
                if(status == null) {
                    status = RocketProtocol.Status.OTHER;
                }
                response = new ResponseWrapper(SysConstants.PROTOCOL_VERSION, RocketProtocol.Phase.PLAINTEXT, protocol.getId(), status, null, null, null);
            }
            log.warn(e.toString());
        } catch (Exception e) {
            log.error("对收到的消息进行解析的时候发生错误", e);
        }

        if(response != null) {
            blasterSender.sendResponse(ctx.channel(), response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
