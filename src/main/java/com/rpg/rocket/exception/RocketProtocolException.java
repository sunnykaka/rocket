package com.rpg.rocket.exception;

import com.rpg.rocket.protocol.RocketProtocol;

/**
 * User: liubin
 * Date: 14-2-28
 */
public class RocketProtocolException extends RocketException {

    public RocketProtocol.Status status;
    public RocketProtocol protocol;

    public RocketProtocolException(String message) {
        super(message);
    }

    public RocketProtocolException(String message, Throwable cause) {
        super(message, cause);
    }

    public RocketProtocolException(Throwable cause) {
        super(cause);
    }

    public RocketProtocolException(RocketProtocol.Status status, RocketProtocol protocol) {
        this.status = status;
        this.protocol = protocol;
    }
}
