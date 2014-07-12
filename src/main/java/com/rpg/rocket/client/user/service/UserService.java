package com.rpg.rocket.client.user.service;

import com.google.protobuf.Message;
import com.rpg.rocket.blaster.message.MessageDispatcher;
import com.rpg.rocket.blaster.message.MessageResponseHandler;
import com.rpg.rocket.blaster.protocol.BlasterProtocol;
import com.rpg.rocket.blaster.protocol.RequestInfo;
import com.rpg.rocket.blaster.protocol.RequestWrapper;
import com.rpg.rocket.blaster.protocol.ResponseInfo;
import com.rpg.rocket.blaster.util.BlasterConstants;
import com.rpg.rocket.client.RocketClient;
import com.rpg.rocket.domain.UserProtos;
import com.rpg.rocket.message.LoginProtos;

/**
 * User: liubin
 * Date: 14-7-1
 */
public class UserService {

    private RocketClient rocketClient;

    public UserService(RocketClient rocketClient) {
        this.rocketClient = rocketClient;
    }

    public UserProtos.User login(String username, String password) {
        MessageDispatcher messageDispatcher = MessageDispatcher.getInstance();
        LoginProtos.LoginRequest.Builder builder = LoginProtos.LoginRequest.newBuilder();
        builder.setUsername(username);
        builder.setPassword(password);
        messageDispatcher.request(
                rocketClient.getChannel(),
                new RequestWrapper(BlasterConstants.PROTOCOL_VERSION,
                    BlasterProtocol.Phase.PLAINTEXT, 3000, null, builder.build()),
                true,
                new MessageResponseHandler() {
                    @Override
                    public void handleResponse(RequestInfo originRequestInfo, Message originMessage, ResponseInfo responseInfo, Message result) {
                        UserProtos.User user = (UserProtos.User) result;
                        System.out.println(user.getId());
                    }

                    @Override
                    public void handleFailure(RequestInfo originRequestInfo, Message originMessage, BlasterProtocol.Status status) {
                        System.out.println(status);
                    }
                });
        return null;

    }


}
