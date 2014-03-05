package com.rpg.rocket.blaster;

import com.google.protobuf.Message;
import com.rpg.rocket.protocol.RocketProtocol;

/**
 * User: liubin
 * Date: 14-3-4
 */
public interface MessageResponseHandler {

    Message handleResponse(RequestInfo originRequestInfo, Message originMessage, ResponseInfo responseInfo, Message result);

    void handleFailureOrTimeout(RequestInfo originRequestInfo, Message originMessage, RocketProtocol.Status status);

}
