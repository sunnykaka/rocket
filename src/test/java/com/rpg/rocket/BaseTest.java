package com.rpg.rocket;


import com.rpg.rocket.client.RocketClient;
import com.rpg.rocket.domain.UserProtos;
import com.rpg.rocket.protocol.RocketProtocol;
import com.rpg.rocket.server.RocketServer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@Test
public class BaseTest {

    protected String host = "localhost";
    protected int port = 8080;

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

    protected RocketProtocol buildProtocol(int version, RocketProtocol.Phase phase, RocketProtocol.Type type, RocketProtocol.Status status,
                                         int timeout, UserProtos.User user) {


        RocketProtocol.Builder protocolBuilder = RocketProtocol.newBuilder();
        protocolBuilder.setVersion(version).setPhase(phase).setType(type).setStatus(status).setTimeout(timeout);
        protocolBuilder.setMessage(user);

        return protocolBuilder.build();
    }

    protected void assertProtocolEquals(RocketProtocol protocol, RocketProtocol decodeProtocol) {
        assertThat(decodeProtocol, notNullValue());
        assertThat(decodeProtocol.getVersion(), is(protocol.getVersion()));
        assertThat(decodeProtocol.getPhase(), is(protocol.getPhase()));
        assertThat(decodeProtocol.getStatus(), is(protocol.getStatus()));
        assertThat(decodeProtocol.getType(), is(protocol.getType()));
        assertThat(decodeProtocol.getId(), is(protocol.getId()));
        assertThat(decodeProtocol.getTimeout(), is(protocol.getTimeout()));
        assertThat(decodeProtocol.getMessageTypeLength(), is(protocol.getMessageTypeLength()));
        assertThat(decodeProtocol.getMessageType(), is(protocol.getMessageType()));
        assertThat(decodeProtocol.getDataLength(), is(protocol.getDataLength()));
        assertThat(true, is(Arrays.equals(protocol.getData(), decodeProtocol.getData())));
    }

}
