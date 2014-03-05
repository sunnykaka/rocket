package com.rpg.rocket.exception;

import com.rpg.rocket.message.BaseMsgProtos;

/**
 * User: liubin
 * Date: 14-3-5
 */
public class AppException extends RuntimeException {

    private BaseMsgProtos.ResponseStatus responseStatus;

    private String msg;

    public AppException(BaseMsgProtos.ResponseStatus responseStatus, String msg) {
        this.responseStatus = responseStatus;
        this.msg = msg;
    }

    public AppException(BaseMsgProtos.ResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }


    public BaseMsgProtos.ResponseStatus getResponseStatus() {
        return responseStatus;
    }

    public String getMsg() {
        return msg;
    }

}
