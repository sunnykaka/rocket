package com.rpg.rocket.blaster;

import com.rpg.rocket.message.BaseMsgProtos;

/**
 * User: liubin
 * Date: 14-3-5
 */
public class ResponseInfo {

    private BaseMsgProtos.ResponseStatus responseStatus;

    private String msg;

    public ResponseInfo(BaseMsgProtos.ResponseStatus responseStatus, String msg) {
        this.responseStatus = responseStatus;
        this.msg = msg;
    }

    public BaseMsgProtos.ResponseStatus getResponseStatus() {
        return responseStatus;
    }

    public String getMsg() {
        return msg;
    }
}
