package com.rpg.rocket;


import com.google.protobuf.Message;
import com.rpg.rocket.blaster.registry.MessageHandlerRegistry;
import com.rpg.rocket.client.RocketClient;
import com.rpg.rocket.domain.UserProtos;
import com.rpg.rocket.message.BaseMsgProtos;
import com.rpg.rocket.protocol.RequestWrapper;
import com.rpg.rocket.protocol.ResponseWrapper;
import com.rpg.rocket.protocol.RocketProtocol;
import com.rpg.rocket.server.RocketServer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@Test
public class BaseTest {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    protected String host = "localhost";
    protected int port = 8080;

    private static int idNumber = 0;

    protected MessageHandlerRegistry messageHandlerRegistry = MessageHandlerRegistry.getInstance();

    @BeforeTest
    public void init() {
        System.out.println("before");
    }

    protected Channel initClient(ChannelInboundHandlerAdapter handler) throws InterruptedException {
        RocketClient rocketClient = new RocketClient();
        Channel channel = rocketClient.connect(host, port, handler);
        return channel;
    }

    protected Channel initServer(ChannelInboundHandlerAdapter handler) throws InterruptedException {
        RocketServer rocketServer = new RocketServer(port);
        Channel channel = rocketServer.accept(handler);
        return channel;
    }

    protected UserProtos.User buildUser() {
        UserProtos.User.Builder user = UserProtos.User.newBuilder();
        user.setId(1234L);
        user.setUsername("asd");
        UserProtos.User.Coordinate.Builder coordinate = UserProtos.User.Coordinate.newBuilder();
        coordinate.setX(12.12f);
        coordinate.setY(32.32f);
        user.setCoordinate(coordinate);
        return user.build();
    }



    protected void checkEncodeAndDecodeRequest(int version, RocketProtocol.Phase phase, int timeout, Long userId, Message message) {

        RequestWrapper request = new RequestWrapper(version, phase, timeout, userId, message);

        ByteBuf buffer = Unpooled.buffer();
        request.getProtocol().encode(buffer);
        RocketProtocol decodeProtocol = RocketProtocol.decode(buffer);

        assertProtocolEquals(request.getProtocol(), decodeProtocol);
    }

    protected void checkEncodeAndDecodeResponse(int version, RocketProtocol.Phase phase, RocketProtocol.Status status, BaseMsgProtos.ResponseStatus responseStatus,
                                            String msg, Message message) {

        ResponseWrapper response = new ResponseWrapper(version, phase, 10, status, responseStatus, msg, message);

        ByteBuf buffer = Unpooled.buffer();
        response.getProtocol().encode(buffer);
        RocketProtocol decodeProtocol = RocketProtocol.decode(buffer);

        assertProtocolEquals(response.getProtocol(), decodeProtocol);
    }

    protected void assertProtocolEquals(RocketProtocol protocol, RocketProtocol decodeProtocol) {
        assertThat(decodeProtocol, notNullValue());
        assertThat(decodeProtocol.getVersion(), is(protocol.getVersion()));
        assertThat(decodeProtocol.getPhase(), is(protocol.getPhase()));
        assertThat(decodeProtocol.getStatus(), is(protocol.getStatus()));
        assertThat(decodeProtocol.getType(), is(protocol.getType()));
        assertThat(decodeProtocol.getId(), is(protocol.getId()));
        assertThat(decodeProtocol.getTimeout(), is(protocol.getTimeout()));
        assertThat(decodeProtocol.getDataLength(), is(protocol.getDataLength()));
        assertThat(true, is(Arrays.equals(protocol.getData(), decodeProtocol.getData())));
        if(RocketProtocol.Type.REQUEST.equals(protocol.getType())) {
            assertThat(new RequestWrapper(protocol).getRequestMsg(), is(new RequestWrapper(decodeProtocol).getRequestMsg()));
        } else {
            assertThat(new ResponseWrapper(protocol).getResponseMsg(), is(new ResponseWrapper(decodeProtocol).getResponseMsg()));
        }
    }

    protected static synchronized int generateId() {
        return ++idNumber;
    }

}
