package com.rpg.rocket.blaster;

import com.google.protobuf.AbstractMessage;
import com.google.protobuf.Message;
import com.rpg.rocket.BaseTest;
import com.rpg.rocket.common.SysConstants;
import com.rpg.rocket.domain.UserProtos;
import com.rpg.rocket.message.BaseMsgProtos;
import com.rpg.rocket.message.LoginProtos;
import com.rpg.rocket.protocol.RequestWrapper;
import com.rpg.rocket.protocol.RocketProtocol;
import com.rpg.rocket.server.NettyRocketProtocolReceiver;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;

import java.util.*;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.notNullValue;

/**
 * User: liubin
 * Date: 14-3-9
 */
@Test
public class BlasterTest extends BaseTest {


    public void test() throws InterruptedException {

        int requestCount = 1;

        BlasterTestTool blasterTestTool = new BlasterTestTool(requestCount);

        List<UserProtos.User> users = blasterTestTool.getUsers();

        messageHandlerRegistry.registerMessageRequestHandler(LoginProtos.LoginRequest.getDescriptor(), new LoginRequestHandler(blasterTestTool));

        BlasterSender blasterSender = new BlasterSender();

        //初始化服务器端
        Channel serverChannel = initServer(new NettyRocketProtocolReceiver());


        Channel clientChannel = initClient(new NettyRocketProtocolReceiver());

        for (int i = 0; i < requestCount ; i++) {
            UserProtos.User user = users.get(i);
            LoginProtos.LoginRequest.Builder loginRequestBuilder = LoginProtos.LoginRequest.newBuilder();
            loginRequestBuilder.setUsername(user.getUsername());
            loginRequestBuilder.setPassword(user.getPassword());

            blasterSender.sendRequest(
                    clientChannel,
                    new RequestWrapper(SysConstants.PROTOCOL_VERSION, RocketProtocol.Phase.PLAINTEXT, 10000, null, loginRequestBuilder.build()),
                    false, new LoginResponseHandler(blasterTestTool));
        }

        Thread.sleep(10000);

        //现在请求应该已经被全部接收,验证结果
        blasterTestTool.validate();

    }

//    static class NettyRocketProtocolReceiverClientTester extends NettyRocketProtocolReceiver {
//
//        @Override
//        public void channelRead(ChannelHandlerContext ctx, Object msg) {
//            RocketProtocol protocol = (RocketProtocol) msg;
//            super.channelRead(ctx, msg);
//        }
//    }

    static class LoginRequestHandler extends AbstractMessageRequestHandler {

        private BlasterTestTool blasterTestTool;

        LoginRequestHandler(BlasterTestTool blasterTestTool) {
            this.blasterTestTool = blasterTestTool;
        }

        @Override
        public Message handleRequest(RequestInfo requestInfo, Message message) {
            LoginProtos.LoginRequest loginRequest = (LoginProtos.LoginRequest) message;
            String username = loginRequest.getUsername();
            String password = loginRequest.getPassword();
            UserProtos.User user = blasterTestTool.getUserByUsernameAndPassword(username, password);

            blasterTestTool.serverReceiverParamsList.add(new Object[]{requestInfo, message});

            return user;
        }

        @Override
        public RocketProtocol.Phase getPhase() {
            return super.getPhase();
        }
    }

    static class LoginResponseHandler extends AbstractMessageResponseHandler {

        private BlasterTestTool blasterTestTool;

        LoginResponseHandler(BlasterTestTool blasterTestTool) {
            this.blasterTestTool = blasterTestTool;
        }

        @Override
        public void handleResponse(RequestInfo originRequestInfo, Message originMessage, ResponseInfo responseInfo, Message result) {

            blasterTestTool.clientMessageResponseParamsList.add(new Object[]{originRequestInfo, originMessage, responseInfo, result});
        }
    }

    static class BlasterTestTool {
        private Map<String, UserProtos.User> userMap = new LinkedHashMap<>();
        private List<UserProtos.User> users = new ArrayList<>();

        public List<Object[]> serverReceiverParamsList = new ArrayList<>();
        public List<Object[]> clientMessageResponseParamsList = new ArrayList<>();

        public BlasterTestTool(int count) {
            for (int i = 1; i <= count; i++) {
                String username = RandomStringUtils.randomAlphabetic(8);
                String password = RandomStringUtils.randomAlphabetic(16);
                UserProtos.User.Builder user = UserProtos.User.newBuilder();
                user.setId(generateId());
                user.setUsername(username);
                user.setPassword(password);
                UserProtos.User.Coordinate.Builder coordinate = UserProtos.User.Coordinate.newBuilder();
                coordinate.setX(new Random().nextFloat());
                coordinate.setY(new Random().nextFloat());
                user.setCoordinate(coordinate);
                UserProtos.User result = user.build();
                userMap.put(buildKey(username, password), result);
                users.add(result);
            }
        }

        private String buildKey(String username, String password) {
            return username + "_" + password;
        }

        public List<UserProtos.User> getUsers() {
//            int index = 0;
//            List<UserProtos.User> users = new ArrayList<>(count);
//            for(Iterator<Map.Entry<String, UserProtos.User>> iterator = userMap.entrySet().iterator(); iterator.hasNext();) {
//                if(index++ >= count) break;
//                users.add(iterator.next().getValue());
//            }
            return users;
        }

        public UserProtos.User getUserByUsernameAndPassword(String username, String password) {
            return userMap.get(buildKey(username, password));
        }

        public void validate() {
            assertThat(serverReceiverParamsList.size(), is(users.size()));
            assertThat(clientMessageResponseParamsList.size(), is(users.size()));

            for (int i = 0; i < users.size(); i++) {
                UserProtos.User user = users.get(i);
                Object[] serverReceiverParams = serverReceiverParamsList.get(i);
                Object[] clientMessageResponseParams = clientMessageResponseParamsList.get(i);

                RequestInfo requestInfo = (RequestInfo) serverReceiverParams[0];
                LoginProtos.LoginRequest loginRequest = (LoginProtos.LoginRequest) serverReceiverParams[1];

                RequestInfo originRequestInfo = (RequestInfo) clientMessageResponseParams[0];
                LoginProtos.LoginRequest originLoginRequest = (LoginProtos.LoginRequest) clientMessageResponseParams[1];
                ResponseInfo responseInfo = (ResponseInfo)clientMessageResponseParams[2];
                LoginProtos.LoginResponse loginResponse = (LoginProtos.LoginResponse) clientMessageResponseParams[3];

                //assert request in server
                assertThat(requestInfo, notNullValue());

                assertThat(loginRequest, notNullValue());
                assertThat(loginRequest.getUsername(), is(user.getUsername()));
                assertThat(loginRequest.getPassword(), is(user.getPassword()));

                //assert message response in client
                assertThat(originRequestInfo, notNullValue());
                assertThat(originRequestInfo, is(requestInfo));

                assertThat(originLoginRequest, notNullValue());
                assertThat(originLoginRequest, is(loginRequest));

                assertThat(responseInfo, notNullValue());
                assertThat(responseInfo.getResponseStatus(), notNullValue());
                assertThat(responseInfo.getResponseStatus(), is(BaseMsgProtos.ResponseStatus.SUCCESS));

                assertThat(loginResponse, notNullValue());
                assertThat(loginResponse.getUser(), notNullValue());
                assertThat(loginResponse.getUser(), is(user));


            }
        }
    }

}
