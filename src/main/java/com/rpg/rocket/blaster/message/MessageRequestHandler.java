package com.rpg.rocket.blaster.message;

import com.google.protobuf.Message;
import com.rpg.rocket.blaster.protocol.BlasterProtocol;
import com.rpg.rocket.blaster.protocol.RequestInfo;

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
    BlasterProtocol.Phase getPhase();

}
