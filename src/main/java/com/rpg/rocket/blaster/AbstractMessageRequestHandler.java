package com.rpg.rocket.blaster;

import com.google.protobuf.Message;
import com.rpg.rocket.protocol.RocketProtocol;

/**
 * User: liubin
 * Date: 14-3-8
 */
public abstract class AbstractMessageRequestHandler implements MessageRequestHandler {

    @Override
    public abstract Message handleRequest(RequestInfo requestInfo, Message message);

    @Override
    public RocketProtocol.Phase getPhase() {
        return RocketProtocol.Phase.PLAINTEXT;
    }
}
