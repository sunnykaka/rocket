package com.rpg.rocket.blaster;

import com.rpg.rocket.protocol.RequestWrapper;
import com.rpg.rocket.protocol.ResponseWrapper;
import com.rpg.rocket.protocol.RocketProtocol;

/**
 * User: liubin
 * Date: 14-3-7
 */
public class MessageResponseDispatcher {

    public static void handleResponse(RequestWrapper originRequest, ResponseWrapper response, MessageResponseHandler messageResponseHandler) {
        RocketProtocol.Status status = RocketProtocol.Status.TIMEOUT;
        if(response != null) {
            status = response.getProtocol().getStatus();
        }

        if(RocketProtocol.Status.SUCCESS.equals(status)) {
            messageResponseHandler.handleResponse(originRequest.getRequestInfo(), originRequest.getMessage(), response.getResponseInfo(), response.getMessage());
        } else {
            messageResponseHandler.handleFailure(originRequest.getRequestInfo(), originRequest.getMessage(), status);
        }
    }


}
