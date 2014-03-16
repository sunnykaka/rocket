package com.rpg.rocket.blaster.message;

import com.google.protobuf.Message;
import com.rpg.rocket.blaster.protocol.BlasterProtocol;
import com.rpg.rocket.blaster.protocol.RequestInfo;
import com.rpg.rocket.blaster.protocol.ResponseInfo;

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

    /**
     * 请求失败的时候会执行该方法
     * @param originRequestInfo 不会为null
     * @param originMessage 不会为null
     * @param status 不会为null
     */
    void handleFailure(RequestInfo originRequestInfo, Message originMessage, BlasterProtocol.Status status);

}
