package com.rpg.rocket.blaster;

import com.google.protobuf.Message;
import com.rpg.rocket.protocol.RocketProtocol;

/**
 * User: liubin
 * Date: 14-3-4
 */
public interface MessageRequestHandler {

    Message handleRequest(RequestInfo requestInfo, Message message);

    RocketProtocol.Phase getPhase();

}
