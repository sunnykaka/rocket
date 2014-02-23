package com.rpg.rocket.protocol;

import com.google.common.primitives.UnsignedBytes;
import com.google.common.primitives.UnsignedInts;
import io.netty.buffer.ByteBuf;

/**
 * User: liubin
 * Date: 14-2-19
 */
public class RocketProtocol {

    public static final int HEAD_LENGTH = 1 + 1 + 1 + 1 + 4 + 4 + 4 + 8 + 8;

    private RocketProtocol(int version, int phase, int status, int type, int id, int messageType, int dataLength, long timeout, long keepData, byte[] data) {
        this.version = version;
        this.phase = phase;
        this.status = status;
        this.type = type;
        this.id = id;
        this.messageType = messageType;
        this.dataLength = dataLength;
        this.timeout = timeout;
        this.keepData = keepData;
        this.data = data;
    }

    private int version;

    private int phase;

    private int status;

    private int type;

    private int id;

    private int messageType;

    private int dataLength;

    private long timeout;

    //kept 8 bytes in head
    private long keepData;

    private byte[] data;


    public static RocketProtocol decode(ByteBuf in) {
        if(in.readableBytes() < HEAD_LENGTH) return null;
        int originReaderIndex = in.readerIndex();
        int version = UnsignedBytes.toInt(in.readByte());
        int phase = UnsignedBytes.toInt(in.readByte());
        int status = UnsignedBytes.toInt(in.readByte());
        int type = UnsignedBytes.toInt(in.readByte());
        int id = in.readInt();
        int messageType = in.readInt();
        int dataLength = in.readInt();
        long timeout = in.readLong();
        long keepData = in.readLong();
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
        RocketProtocol protocol = new RocketProtocol(version, phase, status, type, id, messageType, dataLength, timeout, keepData, data);
        return protocol;
    }

    public void encode(ByteBuf out) {
        out.writeByte((byte)version);
        out.writeByte((byte)phase);
        out.writeByte((byte)status);
        out.writeByte((byte)type);
        out.writeInt(id);
        out.writeInt(messageType);
        out.writeInt(dataLength);
        out.writeLong(timeout);
        out.writeLong(keepData);
        if(dataLength > 0) {
            out.writeBytes(data, 0, dataLength);
        }
    }

    public int getVersion() {
        return version;
    }

    public int getPhase() {
        return phase;
    }

    public int getStatus() {
        return status;
    }

    public int getType() {
        return type;
    }

    public long getId() {
        return id;
    }

    public long getTimeout() {
        return timeout;
    }

    public long getMessageType() {
        return messageType;
    }

    public int getDataLength() {
        return dataLength;
    }

    public byte[] getData() {
        return data;
    }
}
