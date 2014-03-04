package com.rpg.rocket.protocol;

import com.google.common.base.Charsets;
import com.google.common.primitives.UnsignedBytes;
import com.google.protobuf.AbstractMessage;
import com.google.protobuf.Message;
import com.google.protobuf.MessageLite;
import com.rpg.rocket.exception.RocketProtocolException;
import com.rpg.rocket.util.Clock;
import com.rpg.rocket.util.IdGenerator;
import io.netty.buffer.ByteBuf;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * User: liubin
 * Date: 14-2-19
 */
public class RocketProtocol {

    public static final int HEAD_LENGTH = 1 + 1 + 1 + 1 + 4 + 8 + 2 + 4;


    private RocketProtocol() {}

    private RocketProtocol(int version, Phase phase, Status status, Type type, int id, long timeout, int messageTypeLength, int dataLength, String messageType, byte[] data) {
        this.version = version;
        this.phase = phase;
        this.status = status;
        this.type = type;
        this.id = id;
        this.timeout = timeout;
        this.messageTypeLength = messageTypeLength;
        this.dataLength = dataLength;
        this.messageType = messageType;
        this.data = data;
    }

    /** 协议版本号(0x01) 1byte **/
    private int version;

    /** 当前协议阶段(0x0F明文传输，0x1F加密传输, 0xFF其他) 1byte **/
    private Phase phase;

    /** 状态码(0x01-成功, 0x02-解密失败, 0x03-数据损坏, 0x04-其他错误)  1byte [only response] **/
    private Status status;

    /** 类型（0x0F request，0x1F response）1byte **/
    private Type type;

    /** 请求/响应ID 4byte **/
    private int id;

    /** 超时时间,unix time 8byte [only request] **/
    private long timeout;

    /** 消息类型名称长度 2byte **/
    private int messageTypeLength;

    /** 消息长度 4byte **/
    private int dataLength;

    /** 消息类型名称长度 **/
    private String messageType;

    /** 消息 **/
    private byte[] data;


    public static RocketProtocol decode(ByteBuf in) {
        if(in.readableBytes() < HEAD_LENGTH) return null;
        int originReaderIndex = in.readerIndex();
        int version = UnsignedBytes.toInt(in.readByte());
        Phase phase = Phase.valueOf(UnsignedBytes.toInt(in.readByte()));
        Status status = Status.valueOf(UnsignedBytes.toInt(in.readByte()));
        Type type = Type.valueOf(UnsignedBytes.toInt(in.readByte()));
        int id = in.readInt();
        long timeout = in.readLong();
        int messageTypeLength = in.readShort();
        int dataLength = in.readInt();
        String messageType = null;
        byte[] data = null;
        if(dataLength > 0) {
            if(in.readableBytes() < dataLength + messageTypeLength) {
                //数据没有读取完整
                in.readerIndex(originReaderIndex);
                return null;
            }
            byte[] messageTypeBytes = new byte[messageTypeLength];
            in.readBytes(messageTypeBytes, 0, messageTypeLength);
            messageType = new String(messageTypeBytes, Charsets.UTF_8);
            data = new byte[dataLength];
            in.readBytes(data, 0, dataLength);
        }
        RocketProtocol protocol = new RocketProtocol(version, phase, status, type, id, timeout, messageTypeLength, dataLength, messageType, data);
        return protocol;
    }

    public void encode(ByteBuf out) {
        out.writeByte((byte)version);
        out.writeByte((byte)phase.value);
        out.writeByte(status == null ? 0 : (byte)status.value);
        out.writeByte((byte)type.value);
        out.writeInt(id);
        out.writeLong(timeout);
        out.writeShort(messageTypeLength);
        out.writeInt(dataLength);
        if(messageTypeLength > 0) {
            out.writeBytes(messageType.getBytes(Charsets.UTF_8), 0, messageTypeLength);
        }
        if(dataLength > 0) {
            out.writeBytes(data, 0, dataLength);
        }
    }

    public int getVersion() {
        return version;
    }

    public Phase getPhase() {
        return phase;
    }

    public Status getStatus() {
        return status;
    }

    public Type getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public long getTimeout() {
        return timeout;
    }

    public int getMessageTypeLength() {
        return messageTypeLength;
    }

    public int getDataLength() {
        return dataLength;
    }

    public String getMessageType() {
        return messageType;
    }

    public byte[] getData() {
        return data;
    }

    public enum Phase {

        PLAINTEXT(0x0F),

        CIPHERTEXT(0x1F),

        OTHER(0xFF);

        public int value;

        Phase(int value) {
            this.value = value;
        }

        public static Phase valueOf(int value) {
            if(value == 0) {
                return null;
            }
            for(Phase enumValue : values()) {
                if(value == enumValue.value) {
                    return enumValue;
                }
            }
            return OTHER;
        }

    }

    public enum Status {

        /** 成功 **/
        SUCCESS(1),

        /** 解密失败 **/
        DECIPHER_FAILED(2),

        /** 数据损坏 **/
        DATA_CORRUPT(3),

        /** 不支持的版本号 **/
        INVALID_VERSION(4),

        /** 不支持的协议阶段 **/
        INVALID_PHASE(5),

        /** 未知的消息类型 **/
        UNKNOWN_MESSAGE_TYPE(6),


        /** 其他错误 **/
        OTHER(999);



        public int value;

        Status(int value) {
            this.value = value;
        }

        public static Status valueOf(int value) {
            if(value == 0) {
                return null;
            }
            for(Status enumValue : values()) {
                if(value == enumValue.value) {
                    return enumValue;
                }
            }
            return OTHER;
        }

    }

    public enum Type {

        REQUEST(0x0F),

        RESPONSE(0x1F);

        public int value;

        Type(int value) {
            this.value = value;
        }

        public static Type valueOf(int value) {
            if(value == 0) {
                return null;
            }
            for(Type enumValue : values()) {
                if(value == enumValue.value) {
                    return enumValue;
                }
            }
            return null;
        }

    }

    @Override
    public String toString() {
        return "RocketProtocol{" +
                "version=" + version +
                ", phase=" + phase +
                ", status=" + status +
                ", type=" + type +
                ", id=" + id +
                ", timeout=" + timeout +
                ", messageTypeLength=" + messageTypeLength +
                ", dataLength=" + dataLength +
                ", messageType='" + messageType + '\'' +
                '}';
    }

    public static Builder newBuilder() {
        return Builder.create();
    }

    public static final class Builder {

        private RocketProtocol protocol;

        private Builder() {
            protocol = new RocketProtocol();
        }

        private static Builder create() {
            return new Builder();
        }

        public Builder setVersion(int version) {
            protocol.version = version;
            return this;
        }

        public Builder setPhase(Phase phase) {
            protocol.phase = phase;
            return this;
        }

        public Builder setStatus(Status status) {
            protocol.status = status;
            return this;
        }

        public Builder setType(Type type) {
            protocol.type = type;
            return this;
        }

        public Builder setTimeout(Integer millisecond) {
            if(millisecond != null && millisecond > 0) {
                protocol.timeout = Clock.nowInMillisecond() + millisecond;
            }
            return this;
        }

        public Builder setMessage(Message message) {
            if(message != null) {
                String messageType = message.getDescriptorForType().getFullName();
                byte[] data = message.toByteArray();
                if(protocol.messageType != null || protocol.messageTypeLength != 0 ||
                        protocol.data != null || protocol.dataLength != 0) {
                    throw new RocketProtocolException("创建RocketProtocol失败,为protocol添加message的时候发现内部data或messageType不为空");
                }
                protocol.messageType = messageType;
                protocol.messageTypeLength = messageType.getBytes(Charsets.UTF_8).length;
                protocol.data = data;
                protocol.dataLength = data.length;
            }
            return this;
        }

        public RocketProtocol build() {
            if(Type.REQUEST.equals(protocol.type)) {
                protocol.id = IdGenerator.getRequestId();
            }
            return protocol;
        }
    }

}
