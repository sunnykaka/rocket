package com.rpg.rocket.protocol;


import com.rpg.rocket.BaseTest;
import com.rpg.rocket.blaster.protocol.BlasterProtocol;
import com.rpg.rocket.blaster.protocol.RequestWrapper;
import com.rpg.rocket.blaster.protocol.ResponseWrapper;
import com.rpg.rocket.domain.UserProtos;
import com.rpg.rocket.message.BaseMsgProtos;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

@Test
public class BlasterProtocolTest extends BaseTest{

    @BeforeTest
    public void init() {
        super.init();
    }

    @Test
    public void testRequestNormal() throws Exception{
        UserProtos.User user = buildUser();

        checkEncodeAndDecodeRequest(1, BlasterProtocol.Phase.PLAINTEXT, 60, null, user);
        checkEncodeAndDecodeRequest(1, BlasterProtocol.Phase.CIPHERTEXT, 0, 123L, user);

    }

    @Test
    public void testResponseNormal() throws Exception{
        UserProtos.User user = buildUser();

        checkEncodeAndDecodeResponse(1, BlasterProtocol.Phase.CIPHERTEXT, BlasterProtocol.Status.SUCCESS, BaseMsgProtos.ResponseStatus.SUCCESS, "haha", user);
        checkEncodeAndDecodeResponse(1, BlasterProtocol.Phase.CIPHERTEXT, BlasterProtocol.Status.SUCCESS, BaseMsgProtos.ResponseStatus.UNKNOWN_ERROR, "haha", user);
        checkEncodeAndDecodeResponse(1, BlasterProtocol.Phase.PLAINTEXT, BlasterProtocol.Status.DATA_CORRUPT, BaseMsgProtos.ResponseStatus.SUCCESS, "haha", user);

    }

    @Test
    public void testRequestEmptyData() throws Exception{

        checkEncodeAndDecodeRequest(1, BlasterProtocol.Phase.PLAINTEXT, 0, null, buildUser());

    }

    @Test
    public void testResponseEmptyData() throws Exception{

        checkEncodeAndDecodeResponse(1, BlasterProtocol.Phase.CIPHERTEXT, BlasterProtocol.Status.DATA_CORRUPT, BaseMsgProtos.ResponseStatus.UNKNOWN_ERROR, "haha", null);

    }

    @Test
    public void testMultipleProtocolInOneBuffer() throws Exception{


        RequestWrapper request1 = new RequestWrapper(1, BlasterProtocol.Phase.PLAINTEXT, 10000, 123L, buildUser());
        ResponseWrapper response2 = new ResponseWrapper(1, BlasterProtocol.Phase.CIPHERTEXT, 10, BlasterProtocol.Status.DATA_CORRUPT, null, null, null);
        ResponseWrapper response3 = new ResponseWrapper(1, BlasterProtocol.Phase.PLAINTEXT, 10, BlasterProtocol.Status.SUCCESS, BaseMsgProtos.ResponseStatus.USERNAME_NOT_EXIST, "haha", null);
        ResponseWrapper response4 = new ResponseWrapper(1, BlasterProtocol.Phase.PLAINTEXT, 10, BlasterProtocol.Status.SUCCESS, BaseMsgProtos.ResponseStatus.SUCCESS, null, buildUser());

        BlasterProtocol protocol1 = request1.getProtocol();
        BlasterProtocol protocol2 = response2.getProtocol();
        BlasterProtocol protocol3 = response3.getProtocol();
        BlasterProtocol protocol4 = response4.getProtocol();

        ByteBuf buffer = Unpooled.buffer();
        protocol1.encode(buffer);
        protocol2.encode(buffer);
        protocol3.encode(buffer);
        protocol4.encode(buffer);
        BlasterProtocol decodeProtocol1 = BlasterProtocol.decode(buffer);
        BlasterProtocol decodeProtocol2 = BlasterProtocol.decode(buffer);
        BlasterProtocol decodeProtocol3 = BlasterProtocol.decode(buffer);
        BlasterProtocol decodeProtocol4 = BlasterProtocol.decode(buffer);

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
