package com.rpg.rocket.blaster.exception;

import com.rpg.rocket.blaster.protocol.BlasterProtocol;

/**
 * User: liubin
 * Date: 14-2-28
 */
public class BlasterProtocolException extends BlasterException {

    public BlasterProtocol.Status status;
    public BlasterProtocol protocol;

    public BlasterProtocolException(String message) {
        super(message);
    }

    public BlasterProtocolException(String message, Throwable cause) {
        super(message, cause);
    }

    public BlasterProtocolException(Throwable cause) {
        super(cause);
    }

    public BlasterProtocolException(BlasterProtocol.Status status, BlasterProtocol protocol) {
        this(String.format("status[%s], protocol[%s]", status, protocol));
        this.status = status;
        this.protocol = protocol;
    }

    public BlasterProtocol.Status getStatus() {
        return status;
    }

    public BlasterProtocol getProtocol() {
        return protocol;
    }
}
