package com.rpg.rocket.protocol;


import com.rpg.rocket.BaseTest;
import com.rpg.rocket.domain.UserProtos;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

@Test
public class RocketProtocolTest extends BaseTest{

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

}
