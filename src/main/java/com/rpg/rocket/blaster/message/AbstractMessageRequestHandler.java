package com.rpg.rocket.blaster.message;

import com.google.protobuf.Message;
import com.rpg.rocket.blaster.protocol.BlasterProtocol;
import com.rpg.rocket.blaster.protocol.RequestInfo;

/**
 * User: liubin
 * Date: 14-3-8
 */
public abstract class AbstractMessageRequestHandler implements MessageRequestHandler {

    @Override
    public abstract Message handleRequest(RequestInfo requestInfo, Message message);

    @Override
    public BlasterProtocol.Phase getPhase() {
        return BlasterProtocol.Phase.PLAINTEXT;
    }
}
