package com.rpg.rocket.protocol;

import com.google.common.primitives.UnsignedBytes;
import com.google.common.primitives.UnsignedInts;
import io.netty.buffer.ByteBuf;

/**
 * User: liubin
 * Date: 14-2-19
 */
public class RocketProtocol {

    public static final int HEAD_LENGTH = 1 + 1 + 1 + 1 + 4 + 4 + 4 + 4 + 4;

    private RocketProtocol(int version, int phase, int status, int type, long id, long timeout, long protocolId, int dataLength, byte[] data) {
        this.version = version;
        this.phase = phase;
        this.status = status;
        this.type = type;
        this.id = id;
        this.timeout = timeout;
        this.protocolId = protocolId;
        this.dataLength = dataLength;
        this.data = data;
    }

    private int version;

    private int phase;

    private int status;

    private int type;

    private long id;

    private long timeout;

    private long protocolId;

    private int dataLength;

    //kept 4 bytes in head
    //private long keepData;

    private byte[] data;


    public static RocketProtocol decode(ByteBuf in) {
        if(in.readableBytes() < HEAD_LENGTH) return null;
        int originReaderIndex = in.readerIndex();
        int version = UnsignedBytes.toInt(in.readByte());
        int phase = UnsignedBytes.toInt(in.readByte());
        int status = UnsignedBytes.toInt(in.readByte());
        int type = UnsignedBytes.toInt(in.readByte());
        long id = UnsignedInts.toLong(in.readInt());
        long timeout = UnsignedInts.toLong(in.readInt());
        long protocolId = UnsignedInts.toLong(in.readInt());
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
        RocketProtocol protocol = new RocketProtocol(version, phase, status, type, id, timeout, protocolId, dataLength, data);
        return protocol;
    }

    public void encode(ByteBuf out) {
        out.writeByte((byte)version);
        out.writeByte((byte)phase);
        out.writeByte((byte)status);
        out.writeByte((byte)type);
        out.writeInt((int) id);
        out.writeInt((int) timeout);
        out.writeInt((int) protocolId);
        out.writeInt(dataLength);
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

    public long getProtocolId() {
        return protocolId;
    }

    public int getDataLength() {
        return dataLength;
    }

    public byte[] getData() {
        return data;
    }
}
