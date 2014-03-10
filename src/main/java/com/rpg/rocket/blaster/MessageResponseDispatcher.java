package com.rpg.rocket.blaster;

import com.rpg.rocket.protocol.RequestWrapper;
import com.rpg.rocket.protocol.ResponseWrapper;
import com.rpg.rocket.protocol.RocketProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: liubin
 * Date: 14-3-7
 */
public class MessageResponseDispatcher {

    private static final Logger log = LoggerFactory.getLogger(MessageResponseDispatcher.class);

    public static void handleResponse(RequestWrapper originRequest, ResponseWrapper response, MessageResponseHandler messageResponseHandler) {

        RocketProtocol.Status status = RocketProtocol.Status.TIMEOUT;
        if(response != null) {
            status = response.getProtocol().getStatus();
        }

        if(log.isDebugEnabled()) {
            log.debug("开始处理请求的返回结果, requestId[{}], responseId[{}], responseStatus[{}]", new Object[]{originRequest.getProtocol().getId(),
                    response == null ? "null" : response.getProtocol().getId(), status});
        }

        if(RocketProtocol.Status.SUCCESS.equals(status)) {
            messageResponseHandler.handleResponse(originRequest.getRequestInfo(), originRequest.getMessage(), response.getResponseInfo(), response.getMessage());
        } else {
            messageResponseHandler.handleFailure(originRequest.getRequestInfo(), originRequest.getMessage(), status);
        }
    }


}
