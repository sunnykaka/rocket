package com.rpg.rocket.protocol;

import com.google.common.base.Preconditions;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.rpg.rocket.blaster.RequestInfo;
import com.rpg.rocket.exception.RocketProtocolException;
import com.rpg.rocket.message.BaseMsgProtos;

/**
 * User: liubin
 * Date: 14-3-4
 */
public class RequestWrapper {

    private RocketProtocol protocol;

    private BaseMsgProtos.RequestMsg requestMsg;

    private Message message;

    private RequestInfo requestInfo;

    public RequestWrapper(RocketProtocol protocol) {
        this.protocol = protocol;
        Preconditions.checkArgument(RocketProtocol.Type.REQUEST.equals(protocol.getType()));
        byte[] data = protocol.getData();
        if(data == null) return;
        //TODO 不允许data为空
        try {
            this.requestMsg = BaseMsgProtos.RequestMsg.parseFrom(data);
        } catch (InvalidProtocolBufferException e) {
            throw new RocketProtocolException(RocketProtocol.Status.DATA_CORRUPT, protocol);
        }

        this.message = ProtocolUtil.parseMessageFromDataAndType(requestMsg.getMessageType(), requestMsg.getMessage().toByteArray());

        this.requestInfo = new RequestInfo(requestMsg.getUserId());
    }

    public RequestWrapper(int version, RocketProtocol.Phase phase, int timeout, Long userId, Message message) {
        Preconditions.checkNotNull(phase);
        Preconditions.checkNotNull(message);

        RocketProtocol.Builder protocol = RocketProtocol.newBuilder();
        protocol.setPhase(phase);
        protocol.setVersion(version);
        protocol.setTimeout(timeout);

        BaseMsgProtos.RequestMsg.Builder requestMsg = BaseMsgProtos.RequestMsg.newBuilder();

        requestMsg.setMessageType(message.getDescriptorForType().getFullName());
        requestMsg.setMessage(message.toByteString());
        if(userId != null) {
            requestMsg.setUserId(userId);
        }
        this.requestMsg = requestMsg.build();
        protocol.setMessage(this.requestMsg);
        this.protocol = protocol.build();
        this.message = message;
        requestInfo = new RequestInfo(requestMsg.getUserId());

    }

    public BaseMsgProtos.RequestMsg getRequestMsg() {
        return requestMsg;
    }

    public RocketProtocol getProtocol() {
        return protocol;
    }

    public Message getMessage() {
        return message;
    }

    public RequestInfo getRequestInfo() {
        return requestInfo;
    }

    @Override
    public String toString() {
        return "RequestWrapper{" +
                "protocol=" + protocol +
                ", requestMsg=" + requestMsg +
                ", message=" + message +
                ", requestInfo=" + requestInfo +
                '}';
    }
}
