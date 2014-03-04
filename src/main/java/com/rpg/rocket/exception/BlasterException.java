package com.rpg.rocket.exception;

import com.rpg.rocket.protocol.RocketProtocol;

/**
 * User: liubin
 * Date: 14-2-28
 */
public class BlasterException extends RocketException {


    public BlasterException(String message) {
        super(message);
    }

    public BlasterException(String message, Throwable cause) {
        super(message, cause);
    }

    public BlasterException(Throwable cause) {
        super(cause);
    }
}
