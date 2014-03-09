package com.rpg.rocket.blaster;

import com.google.protobuf.Message;
import com.rpg.rocket.protocol.RocketProtocol;

/**
 * User: liubin
 * Date: 14-3-8
 */
public abstract class AbstractMessageResponseHandler implements MessageResponseHandler {
    @Override
    public abstract void handleResponse(RequestInfo originRequestInfo, Message originMessage, ResponseInfo responseInfo, Message result);

    @Override
    public void handleFailure(RequestInfo originRequestInfo, Message originMessage, RocketProtocol.Status status) {
    }
}
