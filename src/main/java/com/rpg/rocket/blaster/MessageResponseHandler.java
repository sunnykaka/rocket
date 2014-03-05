package com.rpg.rocket.blaster;

import com.google.protobuf.Message;
import com.rpg.rocket.protocol.RocketProtocol;

/**
 * User: liubin
 * Date: 14-3-4
 */
public interface MessageResponseHandler {

    /**
     * 对请求的返回结果进行处理的方法
     * @param originRequestInfo 不会为null
     * @param originMessage 不会为null
     * @param responseInfo 不会为null
     * @param result 不会为null
     * @return
     */
    void handleResponse(RequestInfo originRequestInfo, Message originMessage, ResponseInfo responseInfo, Message result);

    void handleFailure(RequestInfo originRequestInfo, Message originMessage, RocketProtocol.Status status);

}
