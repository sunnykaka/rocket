package com.rpg.rocket.blaster.protocol;

import com.google.common.base.Preconditions;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.rpg.rocket.blaster.exception.BlasterProtocolException;
import com.rpg.rocket.message.BaseMsgProtos;

/**
 * User: liubin
 * Date: 14-3-4
 */
public class RequestWrapper {

    private BlasterProtocol protocol;

    private BaseMsgProtos.RequestMsg requestMsg;

    private Message message;

    private RequestInfo requestInfo;

    public RequestWrapper(BlasterProtocol protocol) {
        this.protocol = protocol;
        Preconditions.checkArgument(BlasterProtocol.Type.REQUEST.equals(protocol.getType()));
        byte[] data = protocol.getData();
        if(data == null) return;
        //TODO 不允许data为空
        try {
            this.requestMsg = BaseMsgProtos.RequestMsg.parseFrom(data);
        } catch (InvalidProtocolBufferException e) {
            throw new BlasterProtocolException(BlasterProtocol.Status.DATA_CORRUPT, protocol);
        }

        this.message = ProtocolUtil.parseMessageFromDataAndType(requestMsg.getMessageType(), requestMsg.getMessage().toByteArray());

        this.requestInfo = new RequestInfo(requestMsg.getUserId());
    }

    public RequestWrapper(int version, BlasterProtocol.Phase phase, int timeout, Long userId, Message message) {
        Preconditions.checkNotNull(phase);
        Preconditions.checkNotNull(message);

        BlasterProtocol.Builder protocol = BlasterProtocol.newBuilder();
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

    public BlasterProtocol getProtocol() {
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
