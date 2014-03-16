package com.rpg.rocket.blaster.exception;

import com.rpg.rocket.message.BaseMsgProtos;

/**
 * User: liubin
 * Date: 14-3-5
 */
public class BlasterBusinessException extends RuntimeException {

    private BaseMsgProtos.ResponseStatus responseStatus;

    private String msg;

    public BlasterBusinessException(BaseMsgProtos.ResponseStatus responseStatus, String msg) {
        this.responseStatus = responseStatus;
        this.msg = msg;
    }

    public BlasterBusinessException(BaseMsgProtos.ResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }


    public BaseMsgProtos.ResponseStatus getResponseStatus() {
        return responseStatus;
    }

    public String getMsg() {
        return msg;
    }

}
