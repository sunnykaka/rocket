package com.rpg.rocket.blaster.message;

import com.google.protobuf.Message;
import com.rpg.rocket.blaster.context.IdContext;
import com.rpg.rocket.blaster.context.MessageContext;
import com.rpg.rocket.blaster.exception.BlasterBusinessException;
import com.rpg.rocket.blaster.exception.BlasterProtocolException;
import com.rpg.rocket.blaster.protocol.BlasterProtocol;
import com.rpg.rocket.blaster.protocol.RequestWrapper;
import com.rpg.rocket.blaster.protocol.ResponseWrapper;
import com.rpg.rocket.blaster.registry.MessageHandlerRegistry;
import com.rpg.rocket.blaster.util.BlasterConstants;
import com.rpg.rocket.blaster.util.Clock;
import com.rpg.rocket.message.BaseMsgProtos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;

/**
 * 消息接收者
 * User: liubin
 * Date: 14-3-3
 */
public class BlasterReceiver {

    private static final Logger log = LoggerFactory.getLogger(BlasterReceiver.class);

    private static final BlasterReceiver INSTANCE = new BlasterReceiver();
    private BlasterReceiver() {}
    public static final BlasterReceiver getInstance() {
        return INSTANCE;
    }

    private MessageHandlerRegistry messageHandlerRegistry = MessageHandlerRegistry.getInstance();

    /**
     * 接收消息
     * @param protocol
     * @return
     * @throws BlasterProtocolException
     */
    public ResponseWrapper receive(BlasterProtocol protocol) throws BlasterProtocolException {

        int id = protocol.getId();

        //如果已超时,不做处理
        if(Clock.isTimeout(protocol.getTimeout())) {
            return null;
        }

        if(log.isDebugEnabled()) {
            log.debug("接收到{}, id[{}], protocol[{}]", (BlasterProtocol.Type.REQUEST.equals(protocol.getType()) ? "请求" : "响应"), id, protocol);
        }

        boolean decipher = false;

        if(protocol.getVersion() != 1) {
            throw new BlasterProtocolException(BlasterProtocol.Status.INVALID_VERSION, protocol);
        }

        if(BlasterProtocol.Phase.PLAINTEXT.equals(protocol.getPhase())) {

        } else if(BlasterProtocol.Phase.CIPHERTEXT.equals(protocol.getPhase())) {
            decipher = true;
        } else {
            throw new BlasterProtocolException(BlasterProtocol.Status.INVALID_PHASE, protocol);
        }



        if(decipher) {
            //TODO decipher
        }

        //如果已超时,不做处理
        if(Clock.isTimeout(protocol.getTimeout())) {
            return null;
        }

        if(BlasterProtocol.Type.REQUEST.equals(protocol.getType())) {

            //接收到的是请求消息,执行对应业务逻辑,如果有需要,返回响应结果
            return handleRequest(new RequestWrapper(protocol));

        } else {

            //接收到的是响应消息,查看是否注册了回调函数,有的话进行处理
            handleResponse(new ResponseWrapper(protocol));
            return null;
        }


    }

    /**
     * 处理请求消息
     * @param request
     * @return
     */
    protected ResponseWrapper handleRequest(RequestWrapper request) {
        int id = request.getProtocol().getId();
        long timeout = request.getProtocol().getTimeout();

        BaseMsgProtos.RequestMsg requestMsg = request.getRequestMsg();
        Message message = request.getMessage();

        MessageRequestHandler messageRequestHandler = messageHandlerRegistry.getMessageRequestHandler(requestMsg.getMessageType());
        if(messageRequestHandler == null) {
            log.warn("接收到类型为[{}]的消息,但是没有对应的请求处理器,消息内容:{}", requestMsg.getMessageType(), request.toString());
            return null;
        }

        Message result = null;
        boolean exceptionCaught = false;
        BaseMsgProtos.ResponseStatus responseStatus = BaseMsgProtos.ResponseStatus.UNKNOWN_ERROR;
        String responseMsg = null;

        try {
            if(log.isDebugEnabled()) {
                log.debug("执行请求,requestId[{}], request[{}]", id, request);
            }
            //执行业务处理
            result = messageRequestHandler.handleRequest(request.getRequestInfo(), message);
            responseStatus = BaseMsgProtos.ResponseStatus.SUCCESS;
        } catch (BlasterBusinessException e) {
            exceptionCaught = true;
            if(e.getResponseStatus() != null) {
                responseStatus = e.getResponseStatus();
            }
            responseMsg = e.getMsg();
        } catch (Exception e) {
            exceptionCaught = true;
            log.error("", e);
        }
        if(!exceptionCaught && result == null) {
            //无响应消息
            return null;
        }
        //如果已超时,不返回结果
        if(Clock.isTimeout(timeout)) {
            return null;
        }

        return new ResponseWrapper(BlasterConstants.PROTOCOL_VERSION, messageRequestHandler.getPhase(), id, BlasterProtocol.Status.SUCCESS, responseStatus, responseMsg, result);
    }


    /**
     * 处理响应消息
     * @param response
     */
    protected void handleResponse(ResponseWrapper response) {
        int id = response.getProtocol().getId();

        IdContext idContext = MessageContext.getInstance().getContext(id);
        if(idContext == null) {
            log.warn("接收到响应消息,但是根据id查询不到idContext对象,requestId[{}], response[{}]", id, response);
            return;
        }

        if(!idContext.isAsync()) {
            //原始请求是同步的

            TransferQueue<ResponseWrapper> requestWaiterQueue = idContext.getRequestWaiterQueue();
            boolean transferResponseToWaitingThreadSuccess = false;
            try {
                //尝试将响应结果传给等待线程
                transferResponseToWaitingThreadSuccess = requestWaiterQueue.tryTransfer(response,
                        BlasterConstants.RECEIVER_TRANSFER_TO_QUEUE_WATITING_TIME, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                log.error("", e);
            }

            if(!transferResponseToWaitingThreadSuccess)  {
                log.warn("接收到同步响应消息,但是无线程在等待该响应消息,requestId[{}], response[{}]", id, response);
            }
        } else {
            try {
                //原始请求是异步的
                MessageResponseHandler messageResponseHandler = idContext.getMessageResponseHandler();
                if(messageResponseHandler == null) return;

                RequestWrapper originRequest = idContext.getOriginRequest();
                if(originRequest == null) {
                    log.error("接收到异步响应消息,但是根据id查找不到原始请求对象,程序有bug?,requestId[{}], response[{}]", id, response);
                    return;
                }

                try {
                    if(log.isDebugEnabled()) {
                        log.debug("接收到异步响应消息,运行回调函数,requestId[{}], response[{}]", id, response);
                    }
                    BlasterSenderUtil.executeResponseHandler(originRequest, response, messageResponseHandler);
                } catch (Exception e) {
                    log.error("进行异步结果处理的时候发生错误", e);
                }
            } finally {
                //异步请求由接收方或超时处理线程清除数据
                MessageContext.getInstance().removeContext(id);
            }
        }



        return;
    }

}
