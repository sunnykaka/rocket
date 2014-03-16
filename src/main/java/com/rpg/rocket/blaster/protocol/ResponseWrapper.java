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
public class ResponseWrapper {

    private BlasterProtocol protocol;

    private BaseMsgProtos.ResponseMsg responseMsg;

    private Message message;

    private ResponseInfo responseInfo;

    public ResponseWrapper(BlasterProtocol protocol) {
        this.protocol = protocol;
        Preconditions.checkArgument(BlasterProtocol.Type.RESPONSE.equals(protocol.getType()));
        byte[] data = protocol.getData();
        if(data == null) return;
        try {
            this.responseMsg = BaseMsgProtos.ResponseMsg.parseFrom(data);
        } catch (InvalidProtocolBufferException e) {
            throw new BlasterProtocolException(BlasterProtocol.Status.DATA_CORRUPT, protocol);
        }
        if(responseMsg.getMessageType() != null && responseMsg.getMessage() != null) {
            this.message = ProtocolUtil.parseMessageFromDataAndType(responseMsg.getMessageType(), responseMsg.getMessage().toByteArray());
        }

        this.responseInfo = new ResponseInfo(responseMsg.getStatus(), responseMsg.getMsg());

    }

    public ResponseWrapper(int version, BlasterProtocol.Phase phase, int id, BlasterProtocol.Status status, BaseMsgProtos.ResponseStatus responseStatus,
                           String msg, Message message) {
        Preconditions.checkNotNull(phase);
        Preconditions.checkNotNull(status);

        BlasterProtocol.Builder protocol = BlasterProtocol.newBuilder();
        protocol.setPhase(phase);
        protocol.setVersion(version);
        protocol.setStatus(status);
        protocol.setResponseId(id);

        if(BlasterProtocol.Status.SUCCESS.equals(status)) {
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

    public BlasterProtocol getProtocol() {
        return protocol;
    }

    public Message getMessage() {
        return message;
    }

    public ResponseInfo getResponseInfo() {
        return responseInfo;
    }

    @Override
    public String toString() {
        return "ResponseWrapper{" +
                "protocol=" + protocol +
                ", responseMsg=" + responseMsg +
                ", message=" + message +
                ", responseInfo=" + responseInfo +
                '}';
    }
}
