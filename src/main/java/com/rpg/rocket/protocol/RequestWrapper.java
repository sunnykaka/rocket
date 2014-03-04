package com.rpg.rocket.protocol;

import com.google.common.base.Preconditions;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.rpg.rocket.exception.RocketProtocolException;
import com.rpg.rocket.message.BaseMsgProtos;

/**
 * User: liubin
 * Date: 14-3-4
 */
public class RequestWrapper {

    private RocketProtocol protocol;

    private BaseMsgProtos.RequestMsg requestMsg;

    public RequestWrapper(RocketProtocol protocol) {
        this.protocol = protocol;
        Preconditions.checkArgument(RocketProtocol.Type.REQUEST.equals(protocol.getType()));
        byte[] data = protocol.getData();
        if(data == null) return;
        try {
            this.requestMsg = BaseMsgProtos.RequestMsg.parseFrom(data);
        } catch (InvalidProtocolBufferException e) {
            throw new RocketProtocolException(RocketProtocol.Status.DATA_CORRUPT, protocol);
        }
    }

    public RequestWrapper(int version, RocketProtocol.Phase phase, int timeout, Long userId, Message message) {
        Preconditions.checkNotNull(phase);
        Preconditions.checkNotNull(message);

        RocketProtocol.Builder protocol = RocketProtocol.newBuilder();
        protocol.setPhase(phase);
        protocol.setVersion(version);
        protocol.setTimeout(timeout);
        protocol.setType(RocketProtocol.Type.REQUEST);

        BaseMsgProtos.RequestMsg.Builder requestMsg = BaseMsgProtos.RequestMsg.newBuilder();

        requestMsg.setMessageType(message.getDescriptorForType().getFullName());
        requestMsg.setMessage(message.toByteString());
        if(userId != null) {
            requestMsg.setUserId(userId);
        }
        this.requestMsg = requestMsg.build();
        protocol.setMessage(this.requestMsg);
        this.protocol = protocol.build();

    }

    public BaseMsgProtos.RequestMsg getRequestMsg() {
        return requestMsg;
    }

    public RocketProtocol getProtocol() {
        return protocol;
    }
}
