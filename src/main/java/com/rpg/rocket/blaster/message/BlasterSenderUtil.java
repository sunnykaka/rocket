package com.rpg.rocket.blaster.message;

import com.rpg.rocket.blaster.protocol.BlasterProtocol;
import com.rpg.rocket.blaster.protocol.RequestWrapper;
import com.rpg.rocket.blaster.protocol.ResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: liubin
 * Date: 14-3-7
 */
public class BlasterSenderUtil {

    private static final Logger log = LoggerFactory.getLogger(BlasterSenderUtil.class);

    /**
     * 执行响应回调函数
     * @param originRequest
     * @param response
     * @param messageResponseHandler
     */
    public static void executeResponseHandler(RequestWrapper originRequest, ResponseWrapper response, MessageResponseHandler messageResponseHandler) {

        BlasterProtocol.Status status = BlasterProtocol.Status.TIMEOUT;
        if(response != null) {
            status = response.getProtocol().getStatus();
        }

        if(log.isDebugEnabled()) {
            log.debug("开始处理请求的返回结果(也可能是超时或发送失败), requestId[{}], responseId[{}], responseStatus[{}]", new Object[]{originRequest.getProtocol().getId(),
                    response == null ? "null" : response.getProtocol().getId(), status});
        }

        if(BlasterProtocol.Status.SUCCESS.equals(status)) {
            messageResponseHandler.handleResponse(originRequest.getRequestInfo(), originRequest.getMessage(), response.getResponseInfo(), response.getMessage());
        } else {
            messageResponseHandler.handleFailure(originRequest.getRequestInfo(), originRequest.getMessage(), status);
        }
    }


}
