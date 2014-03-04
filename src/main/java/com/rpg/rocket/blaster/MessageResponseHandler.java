package com.rpg.rocket.blaster;

import com.google.protobuf.Message;
import com.rpg.rocket.protocol.RocketProtocol;

/**
 * User: liubin
 * Date: 14-3-4
 */
public interface MessageResponseHandler {

    Message handleResponse(Message originMessage, Message message);

    void handleFailureOrTimeout(Message originMessage, RocketProtocol.Status status);

}
