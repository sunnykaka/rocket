package com.rpg.rocket.blaster.exception;

/**
 * User: liubin
 * Date: 14-2-28
 */
public class BlasterException extends RuntimeException {


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
