package com.rpg.rocket.blaster.protocol;

import com.google.common.primitives.UnsignedBytes;
import com.rpg.rocket.blaster.exception.BlasterProtocolException;
import com.rpg.rocket.message.BaseMsgProtos;
import com.rpg.rocket.blaster.util.Clock;
import com.rpg.rocket.blaster.util.IdGenerator;
import io.netty.buffer.ByteBuf;

/**
 * User: liubin
 * Date: 14-2-19
 */
public class BlasterProtocol {

    public static final int HEAD_LENGTH = 1 + 1 + 1 + 1 + 4 + 8 + 4;


    private BlasterProtocol() {}

    private BlasterProtocol(int version, Phase phase, Status status, Type type, int id, long timeout, int dataLength, byte[] data) {
        this.version = version;
        this.phase = phase;
        this.status = status;
        this.type = type;
        this.id = id;
        this.timeout = timeout;
        this.dataLength = dataLength;
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

    /** 消息长度 4byte **/
    private int dataLength;

    /** 消息 **/
    private byte[] data;


    public static BlasterProtocol decode(ByteBuf in) {
        if(in.readableBytes() < HEAD_LENGTH) return null;
        int originReaderIndex = in.readerIndex();
        int version = UnsignedBytes.toInt(in.readByte());
        Phase phase = Phase.valueOf(UnsignedBytes.toInt(in.readByte()));
        Status status = Status.valueOf(UnsignedBytes.toInt(in.readByte()));
        Type type = Type.valueOf(UnsignedBytes.toInt(in.readByte()));
        int id = in.readInt();
        long timeout = in.readLong();
        int dataLength = in.readInt();
        byte[] data = null;
        if(dataLength > 0) {
            if(in.readableBytes() < dataLength) {
                //数据没有读取完整
                in.readerIndex(originReaderIndex);
                return null;
            }
            data = new byte[dataLength];
            in.readBytes(data, 0, dataLength);
        }
        BlasterProtocol protocol = new BlasterProtocol(version, phase, status, type, id, timeout, dataLength, data);
        return protocol;
    }

    public void encode(ByteBuf out) {
        out.writeByte((byte)version);
        out.writeByte((byte)phase.value);
        out.writeByte(status == null ? 0 : (byte)status.value);
        out.writeByte((byte) type.value);
        out.writeInt(id);
        out.writeLong(timeout);
        out.writeInt(dataLength);
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

    public int getDataLength() {
        return dataLength;
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

        /** 超时 **/
        TIMEOUT(7),

        /** 请求发送失败 **/
        REQUEST_FAILED(8),

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
        return "BlasterProtocol{" +
                "version=" + version +
                ", phase=" + phase +
                ", status=" + status +
                ", type=" + type +
                ", id=" + id +
                ", timeout=" + timeout +
                ", dataLength=" + dataLength +
                '}';
    }

    public static Builder newBuilder() {
        return Builder.create();
    }

    public static final class Builder {

        private BlasterProtocol protocol;

        private Builder() {
            protocol = new BlasterProtocol();
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


        public Builder setTimeout(Integer millisecond) {
            if(millisecond != null && millisecond > 0) {
                protocol.timeout = Clock.nowInMillisecond() + millisecond;
            }
            return this;
        }

        public Builder setResponseId(int id) {
            protocol.type = Type.RESPONSE;
            protocol.id = id;
            return this;
        }

        public Builder setMessage(BaseMsgProtos.RequestMsg requestMsg) {
            if(protocol.data != null || protocol.dataLength != 0) {
                throw new BlasterProtocolException("创建BlasterProtocol失败,为protocol添加message的时候发现内部data或messageType不为空");
            }
            protocol.type = Type.REQUEST;
            if(requestMsg != null) {
                byte[] data = requestMsg.toByteArray();
                protocol.data = data;
                protocol.dataLength = data.length;
            }
            return this;
        }

        public Builder setMessage(BaseMsgProtos.ResponseMsg responseMsg) {
            if(protocol.data != null || protocol.dataLength != 0) {
                throw new BlasterProtocolException("创建BlasterProtocol失败,为protocol添加message的时候发现内部data或messageType不为空");
            }
            protocol.type = Type.RESPONSE;
            if(responseMsg != null) {
                byte[] data = responseMsg.toByteArray();
                protocol.data = data;
                protocol.dataLength = data.length;
            }
            return this;
        }

        public BlasterProtocol build() {
            if(Type.REQUEST.equals(protocol.type)) {
                protocol.id = IdGenerator.getRequestId();
            } else {
                if(protocol.id < 1) {
                    throw new BlasterProtocolException("创建BlasterProtocol失败,返回消息的id属性不能为空");
                }
            }
            return protocol;
        }
    }

}
