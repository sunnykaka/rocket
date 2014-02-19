package com.rpg.rocket.protocol;

import io.netty.buffer.ByteBuf;

/**
 * User: liubin
 * Date: 14-2-19
 */
public class RocketProtocol {

    public static final int HEAD_LENGTH = 1 + 1 + 1 + 1 + 4 + 4 + 4 + 4 + 4;

    private RocketProtocol() {};

    private int version;

    private int phase;

    private int type;

    private long id;

    private long timeout;

    private long protocolId;

    private long dataLength;

    //kept 4 bytes in head
    //private long keepData;

    private byte[] data;


    public static RocketProtocol decode(ByteBuf in) {
        if(in.readableBytes() < HEAD_LENGTH) return null;
    }


}
