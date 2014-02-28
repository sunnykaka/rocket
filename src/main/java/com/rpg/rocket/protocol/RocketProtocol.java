package com.rpg.rocket.protocol;

import com.google.common.base.Charsets;
import com.google.common.primitives.UnsignedBytes;
import com.rpg.rocket.util.Clock;
import com.rpg.rocket.util.IdGenerator;
import io.netty.buffer.ByteBuf;
import org.apache.commons.lang3.StringUtils;

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

    private int version;

    private Phase phase;

    private Status status;

    private Type type;

    private int id;

    private long timeout;

    private int messageTypeLength;

    private int dataLength;

    private String messageType;

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

        private int value;

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

        SUCCESS(1),

        DECIPHER_FAILED(2),

        DATA_CORRUPT(3),

        OTHER(4);

        private int value;

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

        REQUEST(1),

        RESPONSE(2);

        private int value;

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

        public Builder setMessageTypeWithLength(String messageType) {
            protocol.messageType = messageType;
            if(!StringUtils.isBlank(messageType)) {
                protocol.messageTypeLength = messageType.getBytes(Charsets.UTF_8).length;
            }
            return this;
        }

        public Builder setDataWithLength(byte[] data) {
            protocol.data = data;
            if(data != null) {
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
