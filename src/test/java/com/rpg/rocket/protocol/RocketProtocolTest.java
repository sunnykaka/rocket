package com.rpg.rocket.protocol;


import com.rpg.rocket.BaseTest;
import com.rpg.rocket.domain.UserProtos;
import com.rpg.rocket.message.BaseMsgProtos;
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

        checkEncodeAndDecodeRequest(1, RocketProtocol.Phase.PLAINTEXT, 60, null, user);
    }

    @Test
    public void testResponseNormal() throws Exception{
        UserProtos.User user = buildUser();

        checkEncodeAndDecodeResponse(1, RocketProtocol.Phase.CIPHERTEXT, RocketProtocol.Status.SUCCESS, BaseMsgProtos.ResponseStatus.SUCCESS, "haha", user);

    }

    @Test
    public void testRequestEmptyData() throws Exception{

        checkEncodeAndDecodeRequest(1, RocketProtocol.Phase.PLAINTEXT, 0, null, buildUser());

    }

    @Test
    public void testResponseEmptyData() throws Exception{

        checkEncodeAndDecodeResponse(1, RocketProtocol.Phase.CIPHERTEXT, RocketProtocol.Status.DATA_CORRUPT, BaseMsgProtos.ResponseStatus.UNKNOWN_ERROR, "haha", null);

    }

    @Test
    public void testMultipleProtocolInOneBuffer() throws Exception{


        RequestWrapper request1 = new RequestWrapper(1, RocketProtocol.Phase.PLAINTEXT, 10000, 123L, buildUser());
        ResponseWrapper response2 = new ResponseWrapper(1, RocketProtocol.Phase.CIPHERTEXT, RocketProtocol.Status.DATA_CORRUPT, null, null, null);
        ResponseWrapper response3 = new ResponseWrapper(1, RocketProtocol.Phase.PLAINTEXT, RocketProtocol.Status.SUCCESS, BaseMsgProtos.ResponseStatus.USERNAME_NOT_EXIST, "haha", null);
        ResponseWrapper response4 = new ResponseWrapper(1, RocketProtocol.Phase.PLAINTEXT, RocketProtocol.Status.SUCCESS, BaseMsgProtos.ResponseStatus.SUCCESS, null, buildUser());

        RocketProtocol protocol1 = request1.getProtocol();
        RocketProtocol protocol2 = response2.getProtocol();
        RocketProtocol protocol3 = response3.getProtocol();
        RocketProtocol protocol4 = response4.getProtocol();

        ByteBuf buffer = Unpooled.buffer();
        protocol1.encode(buffer);
        protocol2.encode(buffer);
        protocol3.encode(buffer);
        protocol4.encode(buffer);
        RocketProtocol decodeProtocol1 = RocketProtocol.decode(buffer);
        RocketProtocol decodeProtocol2 = RocketProtocol.decode(buffer);
        RocketProtocol decodeProtocol3 = RocketProtocol.decode(buffer);
        RocketProtocol decodeProtocol4 = RocketProtocol.decode(buffer);

        assertProtocolEquals(protocol1, decodeProtocol1);
        assertProtocolEquals(protocol2, decodeProtocol2);
        assertProtocolEquals(protocol3, decodeProtocol3);
        assertProtocolEquals(protocol4, decodeProtocol4);

//        assertRequestEquals(request1, new RequestWrapper(decodeProtocol1));
//        assertResponseEquals(response2, new ResponseWrapper(decodeProtocol2));
//        assertResponseEquals(response3, new ResponseWrapper(decodeProtocol3));
//        assertResponseEquals(response4, new ResponseWrapper(decodeProtocol4));

    }

}
