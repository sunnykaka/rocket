package com.rpg.rocket.blaster.message;

import com.google.protobuf.Message;
import com.rpg.rocket.blaster.protocol.BlasterProtocol;
import com.rpg.rocket.blaster.protocol.RequestInfo;
import com.rpg.rocket.blaster.protocol.ResponseInfo;

/**
 * User: liubin
 * Date: 14-3-8
 */
public abstract class AbstractMessageResponseHandler implements MessageResponseHandler {
    @Override
    public abstract void handleResponse(RequestInfo originRequestInfo, Message originMessage, ResponseInfo responseInfo, Message result);

    @Override
    public void handleFailure(RequestInfo originRequestInfo, Message originMessage, BlasterProtocol.Status status) {
    }
}
