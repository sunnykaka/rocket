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
public class ResponseWrapper {

    private RocketProtocol protocol;

    private BaseMsgProtos.ResponseMsg responseMsg;

    public ResponseWrapper(RocketProtocol protocol) {
        this.protocol = protocol;
        Preconditions.checkArgument(RocketProtocol.Type.RESPONSE.equals(protocol.getType()));
        byte[] data = protocol.getData();
        if(data == null) return;
        try {
            this.responseMsg = BaseMsgProtos.ResponseMsg.parseFrom(data);
        } catch (InvalidProtocolBufferException e) {
            throw new RocketProtocolException(RocketProtocol.Status.DATA_CORRUPT, protocol);
        }
    }

    public ResponseWrapper(int version, RocketProtocol.Phase phase, int id, RocketProtocol.Status status, BaseMsgProtos.ResponseStatus responseStatus,
                           String msg, Message message) {
        Preconditions.checkNotNull(phase);
        Preconditions.checkNotNull(status);

        RocketProtocol.Builder protocol = RocketProtocol.newBuilder();
        protocol.setPhase(phase);
        protocol.setVersion(version);
        protocol.setStatus(status);
        protocol.setResponseId(id);

        if(RocketProtocol.Status.SUCCESS.equals(status)) {
            //只有正常解析协议了才设置业务对象
            BaseMsgProtos.ResponseMsg.Builder responseMsg = BaseMsgProtos.ResponseMsg.newBuilder();
            if(responseStatus != null) {
                responseMsg.setStatus(responseStatus);
            }
            if(msg != null) {
                responseMsg.setMsg(msg);
            }
            if(message != null) {
                responseMsg.setMessageType(message.getDescriptorForType().getFullName());
                responseMsg.setMessage(message.toByteString());
            }
            this.responseMsg = responseMsg.build();
            protocol.setMessage(this.responseMsg);
        }
        this.protocol = protocol.build();
    }


    public BaseMsgProtos.ResponseMsg getResponseMsg() {
        return responseMsg;
    }

    public RocketProtocol getProtocol() {
        return protocol;
    }
}
