package com.rpg.rocket.protocol;


import com.rpg.rocket.domain.UserProtos;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

@Test
public class RocketProtocolTest {

    @BeforeTest
    public void init() {
        System.out.println("before");
    }

    @Test
    public void testRequestNormal() throws Exception{
        UserProtos.User user = buildUser();

        RocketProtocol protocol = buildProtocol(0, RocketProtocol.Phase.PLAINTEXT, RocketProtocol.Type.REQUEST, null, 60, user);

        ByteBuf buffer = Unpooled.buffer();
        protocol.encode(buffer);
        RocketProtocol decodeProtocol = RocketProtocol.decode(buffer);

        assertProtocolEquals(protocol, decodeProtocol);

    }

    @Test
    public void testResponseNormal() throws Exception{
        UserProtos.User user = buildUser();

        RocketProtocol protocol = buildProtocol(0, RocketProtocol.Phase.CIPHERTEXT, RocketProtocol.Type.RESPONSE, RocketProtocol.Status.SUCCESS, 0, user);

        ByteBuf buffer = Unpooled.buffer();
        protocol.encode(buffer);
        RocketProtocol decodeProtocol = RocketProtocol.decode(buffer);

        assertProtocolEquals(protocol, decodeProtocol);

    }

    @Test
    public void testRequestEmptyData() throws Exception{

        RocketProtocol protocol = buildProtocol(0, RocketProtocol.Phase.PLAINTEXT, RocketProtocol.Type.REQUEST, null, 0, null);

        ByteBuf buffer = Unpooled.buffer();
        protocol.encode(buffer);
        RocketProtocol decodeProtocol = RocketProtocol.decode(buffer);

        assertProtocolEquals(protocol, decodeProtocol);

    }

    @Test
    public void testResponseEmptyData() throws Exception{

        RocketProtocol protocol = buildProtocol(0, RocketProtocol.Phase.CIPHERTEXT, RocketProtocol.Type.RESPONSE, null, 3000, null);

        ByteBuf buffer = Unpooled.buffer();
        protocol.encode(buffer);
        RocketProtocol decodeProtocol = RocketProtocol.decode(buffer);

        assertProtocolEquals(protocol, decodeProtocol);

    }

    @Test
    public void testMultipleProtocolInOneBuffer() throws Exception{

        RocketProtocol protocol1 = buildProtocol(0, RocketProtocol.Phase.PLAINTEXT, RocketProtocol.Type.REQUEST, null, 10000, buildUser());
        RocketProtocol protocol2 = buildProtocol(0, RocketProtocol.Phase.CIPHERTEXT, RocketProtocol.Type.RESPONSE, RocketProtocol.Status.DATA_CORRUPT, 0, buildUser());
        RocketProtocol protocol3 = buildProtocol(0, RocketProtocol.Phase.PLAINTEXT, RocketProtocol.Type.RESPONSE, RocketProtocol.Status.SUCCESS, 0, null);

        ByteBuf buffer = Unpooled.buffer();
        protocol1.encode(buffer);
        protocol2.encode(buffer);
        protocol3.encode(buffer);
        RocketProtocol decodeProtocol1 = RocketProtocol.decode(buffer);
        RocketProtocol decodeProtocol2 = RocketProtocol.decode(buffer);
        RocketProtocol decodeProtocol3 = RocketProtocol.decode(buffer);

        assertProtocolEquals(protocol1, decodeProtocol1);
        assertProtocolEquals(protocol2, decodeProtocol2);
        assertProtocolEquals(protocol3, decodeProtocol3);


    }

    private UserProtos.User buildUser() {
        UserProtos.User.Builder user = UserProtos.User.newBuilder();
        user.setId(1234L);
        user.setUsername("asd");
        UserProtos.User.Coordinate.Builder coordinate = UserProtos.User.Coordinate.newBuilder();
        coordinate.setX(12.12f);
        coordinate.setY(32.32f);
        user.setCoordinate(coordinate);
        return user.build();
    }

    private RocketProtocol buildProtocol(int version, RocketProtocol.Phase phase, RocketProtocol.Type type, RocketProtocol.Status status,
                                         int timeout, UserProtos.User user) {


        RocketProtocol.Builder protocolBuilder = RocketProtocol.newBuilder();
        protocolBuilder.setVersion(version).setPhase(phase).setType(type).setStatus(status).setTimeout(timeout);
        if(user != null) {
            String messageType = user.getDescriptorForType().getFullName();
            byte[] data = user.toByteArray();
            protocolBuilder.setMessageTypeWithLength(messageType).setDataWithLength(data);
        }

        return protocolBuilder.build();
    }

    private void assertProtocolEquals(RocketProtocol protocol, RocketProtocol decodeProtocol) {
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
