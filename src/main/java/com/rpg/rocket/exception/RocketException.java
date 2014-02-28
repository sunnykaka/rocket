package com.rpg.rocket.exception;

/**
 * User: liubin
 * Date: 14-2-28
 */
public class RocketException extends RuntimeException {

    public RocketException() {
        super();
    }

    public RocketException(String message) {
        super(message);
    }

    public RocketException(String message, Throwable cause) {
        super(message, cause);
    }

    public RocketException(Throwable cause) {
        super(cause);
    }
}
