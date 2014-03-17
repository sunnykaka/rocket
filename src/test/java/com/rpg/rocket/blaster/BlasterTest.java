package com.rpg.rocket.blaster;

import com.google.protobuf.Message;
import com.rpg.rocket.BaseTest;
import com.rpg.rocket.blaster.message.AbstractMessageRequestHandler;
import com.rpg.rocket.blaster.message.AbstractMessageResponseHandler;
import com.rpg.rocket.blaster.message.MessageDispatcher;
import com.rpg.rocket.blaster.protocol.RequestInfo;
import com.rpg.rocket.blaster.protocol.ResponseInfo;
import com.rpg.rocket.blaster.util.BlasterConstants;
import com.rpg.rocket.domain.UserProtos;
import com.rpg.rocket.message.BaseMsgProtos;
import com.rpg.rocket.message.LoginProtos;
import com.rpg.rocket.blaster.protocol.RequestWrapper;
import com.rpg.rocket.blaster.protocol.BlasterProtocol;
import com.rpg.rocket.blaster.netty.handler.NettyBlasterProtocolReceiver;
import io.netty.channel.Channel;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.*;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * User: liubin
 * Date: 14-3-9
 */
public class BlasterTest extends BaseTest {

    @BeforeTest
    @Override
    public void init() {
        super.init();

    }

    @Test
    public void testSyncRequestSuccess() throws InterruptedException {

        runBlasterTest(100, 0, false);

    }

    @Test
    public void testAsyncRequestSuccess() throws InterruptedException {

        runBlasterTest(100, 0, true);

    }


    @Test
    public void testSyncRequestTimeout() throws InterruptedException {

        runBlasterTest(10, 5, false);

    }

    private void runBlasterTest(int requestCount, int timeoutCount, boolean async) throws InterruptedException {

        BlasterTestTool blasterTestTool = new BlasterTestTool(requestCount, timeoutCount);

        List<UserProtos.User> users = blasterTestTool.getUsers();

        MessageDispatcher messageDispatcher = MessageDispatcher.getInstance();

        messageHandlerRegistry.deregisterMessageRequestHandler(LoginProtos.LoginRequest.getDescriptor());
        messageHandlerRegistry.registerMessageRequestHandler(LoginProtos.LoginRequest.getDescriptor(), new LoginRequestHandler(blasterTestTool));

        //初始化服务器端
        Channel serverChannel = initServer(new NettyBlasterProtocolReceiver());

        //初始化客户端
        Channel clientChannel = initClient(new NettyBlasterProtocolReceiver());

        for (int i = 0; i < requestCount; i++) {
            UserProtos.User user = users.get(i);
            LoginProtos.LoginRequest.Builder loginRequestBuilder = LoginProtos.LoginRequest.newBuilder();
            loginRequestBuilder.setUsername(user.getUsername());
            loginRequestBuilder.setPassword(user.getPassword());

            int timeout = 3000;
            if(timeoutCount-- > 0) {
                //最开始的timeoutCount个请求会超时
                timeout = blasterTestTool.requestTimeoutParam;
            }
            messageDispatcher.request(
                    clientChannel,
                    new RequestWrapper(BlasterConstants.PROTOCOL_VERSION, BlasterProtocol.Phase.PLAINTEXT, timeout, null, loginRequestBuilder.build()),
                    async, new LoginResponseHandler(blasterTestTool));
        }

        Thread.sleep(5000);

        //现在请求应该已经被全部接收,验证结果
        blasterTestTool.validate();

        clientChannel.close().sync();
        serverChannel.close().sync();
    }


//    static class NettyRocketProtocolReceiverClientTester extends NettyBlasterProtocolReceiver {
//
//        @Override
//        public void channelRead(ChannelHandlerContext ctx, Object msg) {
//            BlasterProtocol protocol = (BlasterProtocol) msg;
//            super.channelRead(ctx, msg);
//        }
//    }

    private static class LoginRequestHandler extends AbstractMessageRequestHandler {

        protected final Logger log = LoggerFactory.getLogger(this.getClass());

        private BlasterTestTool blasterTestTool;

        LoginRequestHandler(BlasterTestTool blasterTestTool) {
            this.blasterTestTool = blasterTestTool;
        }

        @Override
        public Message handleRequest(RequestInfo requestInfo, Message message) {
            LoginProtos.LoginRequest loginRequest = (LoginProtos.LoginRequest) message;
            log.debug("进入handleRequest,username:" + loginRequest.getUsername());
            String username = loginRequest.getUsername();
            String password = loginRequest.getPassword();
            UserProtos.User user = blasterTestTool.getUserByUsernameAndPassword(username, password);

            if(blasterTestTool.timeoutCount > 0) {
                //如果超时参数不为空,等待指定时间
                try {
                    Thread.sleep(blasterTestTool.receiveWaitingParam);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            blasterTestTool.serverReceiverParamsList.add(new Object[]{requestInfo, message});

            return user;
        }

        @Override
        public BlasterProtocol.Phase getPhase() {
            return super.getPhase();
        }
    }

    private static class LoginResponseHandler extends AbstractMessageResponseHandler {

        private BlasterTestTool blasterTestTool;

        LoginResponseHandler(BlasterTestTool blasterTestTool) {
            this.blasterTestTool = blasterTestTool;
        }

        @Override
        public void handleResponse(RequestInfo originRequestInfo, Message originMessage, ResponseInfo responseInfo, Message result) {

            blasterTestTool.clientMessageResponseSuccessParamsList.add(new Object[]{originRequestInfo, originMessage, responseInfo, result});
        }

        @Override
        public void handleFailure(RequestInfo originRequestInfo, Message originMessage, BlasterProtocol.Status status) {

            blasterTestTool.clientMessageResponseFailureParamsList.add(new Object[]{originRequestInfo, originMessage, status});
        }
    }

    private static class BlasterTestTool {
        private static Logger log = LoggerFactory.getLogger(BlasterTestTool.class);

        //key-根据username和password构建的key,value-user对象
        private Map<String, UserProtos.User> userMap = new LinkedHashMap<>();
        //初始化用户数据以供测试
        private List<UserProtos.User> users = new ArrayList<>();

        //服务器接收到的数据列表
        public List<Object[]> serverReceiverParamsList = new Vector<>();
        //客户端接收到的请求成功的数据列表
        public List<Object[]> clientMessageResponseSuccessParamsList = new Vector<>();
        //客户端接收到的请求失败的数据列表
        public List<Object[]> clientMessageResponseFailureParamsList = new Vector<>();

        //总共测试数量
        private int count = 0;
        //超时请求数量
        private int timeoutCount = 0;
        //客户端请求等待超时时间
        private int requestTimeoutParam = 300;
        //服务器阻塞时间,该时间应该略大于请求线程超时等待时间,但是不能大太多.不然服务器的worker线程被阻塞了,客户端的请求消息会被丢弃
        //FIXME 当该参数值设的过大,导致服务器worker线程被长时间阻塞的时候,为什么在阻塞的这段时间里,
        //      客户端请求的数据发往服务器的时候发送成功,但是当worker线程继续工作时,这段时间的数据全部被丢弃!只会处理之后的数据,为什么?
        private int receiveWaitingParam = requestTimeoutParam + 50;

        public BlasterTestTool(int count, int timeoutCount) {
            this.timeoutCount = timeoutCount;
            this.count = count;
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
            return users;
        }

        public UserProtos.User getUserByUsernameAndPassword(String username, String password) {
            return userMap.get(buildKey(username, password));
        }

        public void validate() {
            log.info("serverReceiverParamsList size:" + serverReceiverParamsList.size());
            assertThat(serverReceiverParamsList.size(), is(users.size()));
            if(timeoutCount <= 0) {
                //正常情况测试
                assertThat(clientMessageResponseSuccessParamsList.size(), is(users.size()));
                assertThat(clientMessageResponseSuccessParamsList.size(), is(count));
                assertThat(clientMessageResponseFailureParamsList.size(), is(0));

                for (int i = 0; i < users.size(); i++) {
                    UserProtos.User user = users.get(i);
                    Object[] serverReceiverParams = serverReceiverParamsList.get(i);
                    Object[] clientMessageResponseParams = clientMessageResponseSuccessParamsList.get(i);

                    assertMessageSendAndReceiveSuccess(user, serverReceiverParams, clientMessageResponseParams);

                }

            } else {
                //超时情况测试
                assertThat(clientMessageResponseSuccessParamsList.size(), is(count - timeoutCount));
                assertThat(clientMessageResponseFailureParamsList.size(), is(timeoutCount));

                int j = timeoutCount;
                for (int i = 0; i < count; i++) {
                    UserProtos.User user = users.get(i);
                    Object[] serverReceiverParams = serverReceiverParamsList.get(i);

                    RequestInfo requestInfo = (RequestInfo) serverReceiverParams[0];
                    LoginProtos.LoginRequest loginRequest = (LoginProtos.LoginRequest) serverReceiverParams[1];

                    if(j > 0) {
                        //确保前timeoutCount个请求的超时响应结果正确
                        Object[] clientMessageResponseFailureParams = clientMessageResponseFailureParamsList.get(timeoutCount - j--);
                        RequestInfo originRequestInfo = (RequestInfo) clientMessageResponseFailureParams[0];
                        LoginProtos.LoginRequest originLoginRequest = (LoginProtos.LoginRequest) clientMessageResponseFailureParams[1];
                        BlasterProtocol.Status status = (BlasterProtocol.Status) clientMessageResponseFailureParams[2];

                        assertThat(originRequestInfo, notNullValue());
                        assertThat(originRequestInfo.getUserId(), is(requestInfo.getUserId()));

                        assertThat(originLoginRequest, notNullValue());
                        assertThat(originLoginRequest, is(loginRequest));

                        assertThat(status, notNullValue());
                        assertThat(status, is(BlasterProtocol.Status.TIMEOUT));

                    } else {
                        //确保后requestCount - timeoutCount个请求的正常请求响应结果正确
                        Object[] clientMessageResponseParams = clientMessageResponseSuccessParamsList.get(i - timeoutCount);

                        assertMessageSendAndReceiveSuccess(user, serverReceiverParams, clientMessageResponseParams);
                    }
                }

            }

        }

        /**
         * 确认消息接收和发送成功
         * @param user
         * @param serverReceiverParams
         * @param clientMessageResponseParams
         */
        private void assertMessageSendAndReceiveSuccess(UserProtos.User user, Object[] serverReceiverParams, Object[] clientMessageResponseParams) {
            RequestInfo requestInfo = (RequestInfo) serverReceiverParams[0];
            LoginProtos.LoginRequest loginRequest = (LoginProtos.LoginRequest) serverReceiverParams[1];

            RequestInfo originRequestInfo = (RequestInfo) clientMessageResponseParams[0];
            LoginProtos.LoginRequest originLoginRequest = (LoginProtos.LoginRequest) clientMessageResponseParams[1];
            ResponseInfo responseInfo = (ResponseInfo)clientMessageResponseParams[2];
            UserProtos.User responseUser = (UserProtos.User) clientMessageResponseParams[3];

            //assert request in server
            assertThat(requestInfo, notNullValue());

            assertThat(loginRequest, notNullValue());
            assertThat(loginRequest.getUsername(), is(user.getUsername()));
            assertThat(loginRequest.getPassword(), is(user.getPassword()));

            //assert message response in client
            assertThat(originRequestInfo, notNullValue());
            assertThat(originRequestInfo.getUserId(), is(requestInfo.getUserId()));

            assertThat(originLoginRequest, notNullValue());
            assertThat(originLoginRequest, is(loginRequest));

            assertThat(responseInfo, notNullValue());
            assertThat(responseInfo.getResponseStatus(), notNullValue());
            assertThat(responseInfo.getResponseStatus(), is(BaseMsgProtos.ResponseStatus.SUCCESS));

            assertThat(responseUser, is(user));
        }
    }

}
