package com.rpg.rocket.blaster;

import com.google.protobuf.Message;
import com.rpg.rocket.protocol.RocketProtocol;

/**
 * User: liubin
 * Date: 14-3-4
 */
public interface MessageRequestHandler {

    /**
     * 处理请求
     * @param requestInfo
     * @param message
     * @return
     */
    Message handleRequest(RequestInfo requestInfo, Message message);

    /**
     * 返回结果是否需要加密
     * @return
     */
    RocketProtocol.Phase getPhase();

}
