package com.rpg.rocket.server;

import com.rpg.rocket.BaseTest;
import com.rpg.rocket.protocol.RocketProtocol;
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
        final List<RocketProtocol> clientProtocolList = new LinkedList<>();
        final List<RocketProtocol> serverProtocolList = new CopyOnWriteArrayList<>();

        RocketProtocol protocol1 = buildProtocol(0, RocketProtocol.Phase.PLAINTEXT, RocketProtocol.Type.REQUEST, null, 10000, buildUser());
        RocketProtocol protocol2 = buildProtocol(0, RocketProtocol.Phase.CIPHERTEXT, RocketProtocol.Type.RESPONSE, RocketProtocol.Status.DATA_CORRUPT, 0, buildUser());
        RocketProtocol protocol3 = buildProtocol(0, RocketProtocol.Phase.PLAINTEXT, RocketProtocol.Type.RESPONSE, RocketProtocol.Status.SUCCESS, 0, null);

        clientProtocolList.add(protocol1);
        clientProtocolList.add(protocol2);
        clientProtocolList.add(protocol3);

        //初始化服务器端
        Channel serverChannel = initServer(new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                RocketProtocol protocol = (RocketProtocol) msg;
                serverProtocolList.add(protocol);
            }
        });

        //初始化客户端
        Channel clientChannel = initClient(null);
        for(RocketProtocol protocol : clientProtocolList) {
            clientChannel.writeAndFlush(protocol);
        }

        //等待服务器接收处理完所有数据
        StopWatch sw = new StopWatch();
        sw.start();
        while(serverProtocolList.size() != clientProtocolList.size()) {
            System.out.println("服务器还没接收完所有数据,等待");
            Thread.sleep(1000);
        }
        sw.stop();
        System.out.println("等待服务器数据完成共耗时:" + sw.toString());

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
