package com.rpg.rocket.server;

import com.rpg.rocket.BaseTest;
import com.rpg.rocket.blaster.netty.channel.NettyChannelInitializer;
import com.rpg.rocket.message.BaseMsgProtos;
import com.rpg.rocket.blaster.protocol.BlasterProtocol;
import com.rpg.rocket.blaster.protocol.RequestWrapper;
import com.rpg.rocket.blaster.protocol.ResponseWrapper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.commons.lang3.time.StopWatch;
import org.testng.annotations.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 * User: liubin
 * Date: 14-3-2
 */
public class NettyServerClientTest extends BaseTest {

    @Test
    public void test() throws InterruptedException {
        final List<BlasterProtocol> clientProtocolList = new LinkedList<>();
        final List<BlasterProtocol> serverProtocolList = new CopyOnWriteArrayList<>();

        RequestWrapper request1 = new RequestWrapper(1, BlasterProtocol.Phase.PLAINTEXT, 10000, 123L, buildUser());
        ResponseWrapper response2 = new ResponseWrapper(1, BlasterProtocol.Phase.CIPHERTEXT, 10, BlasterProtocol.Status.DATA_CORRUPT, null, null, null);
        ResponseWrapper response3 = new ResponseWrapper(1, BlasterProtocol.Phase.PLAINTEXT, 10, BlasterProtocol.Status.SUCCESS, BaseMsgProtos.ResponseStatus.USERNAME_NOT_EXIST, "haha", null);
        ResponseWrapper response4 = new ResponseWrapper(1, BlasterProtocol.Phase.PLAINTEXT, 10, BlasterProtocol.Status.SUCCESS, BaseMsgProtos.ResponseStatus.SUCCESS, null, buildUser());

        clientProtocolList.add(request1.getProtocol());
        clientProtocolList.add(response2.getProtocol());
        clientProtocolList.add(response3.getProtocol());
        clientProtocolList.add(response4.getProtocol());

        //初始化服务器端
        Channel serverChannel = initServer(new NettyChannelInitializer(new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                BlasterProtocol protocol = (BlasterProtocol) msg;
                serverProtocolList.add(protocol);
            }
        }));

        //初始化客户端
        Channel clientChannel = initClient(new NettyChannelInitializer());
        for(BlasterProtocol protocol : clientProtocolList) {
            clientChannel.writeAndFlush(protocol);
        }

        //等待服务器接收处理完所有数据
        StopWatch sw = new StopWatch();
        sw.start();
        while(serverProtocolList.size() != clientProtocolList.size()) {
            log.info("服务器还没接收完所有数据,等待");
            Thread.sleep(1000);
        }
        sw.stop();
        log.info("等待服务器数据完成共耗时:" + sw.toString());

        //到这里写入和读取应该完成
        //判断结果
        assertThat(serverProtocolList.size(), is(clientProtocolList.size()));
        for(int i=0; i<serverProtocolList.size(); i++) {
            assertProtocolEquals(clientProtocolList.get(i), serverProtocolList.get(i));
        }

        clientChannel.close().sync();
        serverChannel.close().sync();


    }



}
