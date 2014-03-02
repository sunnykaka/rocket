package com.rpg.rocket.exception;

/**
 * User: liubin
 * Date: 14-2-28
 */
public class RocketProtocolException extends RocketException {

    public RocketProtocolException() {
        super();
    }

    public RocketProtocolException(String message) {
        super(message);
    }

    public RocketProtocolException(String message, Throwable cause) {
        super(message, cause);
    }

    public RocketProtocolException(Throwable cause) {
        super(cause);
    }
}
