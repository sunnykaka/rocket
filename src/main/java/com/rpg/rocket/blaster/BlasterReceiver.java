package com.rpg.rocket.blaster;

import com.google.protobuf.Message;
import com.rpg.rocket.blaster.registry.MessageHandlerRegistry;
import com.rpg.rocket.common.SysConstants;
import com.rpg.rocket.exception.AppException;
import com.rpg.rocket.exception.RocketProtocolException;
import com.rpg.rocket.message.BaseMsgProtos;
import com.rpg.rocket.protocol.RequestWrapper;
import com.rpg.rocket.protocol.ResponseWrapper;
import com.rpg.rocket.protocol.RocketProtocol;
import com.rpg.rocket.util.Clock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TransferQueue;

/**
 * User: liubin
 * Date: 14-3-3
 */
public class BlasterReceiver {

    private static final Logger log = LoggerFactory.getLogger(BlasterReceiver.class);

    private MessageHandlerRegistry messageHandlerRegistry = MessageHandlerRegistry.getInstance();

    public ResponseWrapper receive(RocketProtocol protocol) throws RocketProtocolException {

        if(log.isDebugEnabled()) {
            log.debug("接收到请求或响应, protocol[{}]", protocol);
        }

        boolean decipher = false;

        if(protocol.getVersion() != 1) {
            throw new RocketProtocolException(RocketProtocol.Status.INVALID_VERSION, protocol);
        }

        if(RocketProtocol.Phase.PLAINTEXT.equals(protocol.getPhase())) {

        } else if(RocketProtocol.Phase.CIPHERTEXT.equals(protocol.getPhase())) {
            decipher = true;
        } else {
            throw new RocketProtocolException(RocketProtocol.Status.INVALID_PHASE, protocol);
        }

        int id = protocol.getId();

        if(decipher) {
            //TODO decipher
        }

        if(RocketProtocol.Type.REQUEST.equals(protocol.getType())) {
            //接收到的是请求消息,执行对应业务逻辑,如果有需要,返回响应结果

            long timeout = protocol.getTimeout();
            if(Clock.isTimeout(timeout)) {
                return null;
            }

            RequestWrapper request = new RequestWrapper(protocol);
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
                    log.debug("执行请求,requestId[{}], request[{}]", protocol.getId(), request);
                }
                result = messageRequestHandler.handleRequest(request.getRequestInfo(), message);
                responseStatus = BaseMsgProtos.ResponseStatus.SUCCESS;
            } catch (AppException e) {
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
                return null;
            }

            return new ResponseWrapper(SysConstants.PROTOCOL_VERSION, messageRequestHandler.getPhase(), id, RocketProtocol.Status.SUCCESS, responseStatus, responseMsg, result);

        } else {
            //接收到的是响应消息,根据id查看是否有同步等待队列或者注册了异步回调函数,有的话进行处理

            ResponseWrapper response = new ResponseWrapper(protocol);

            //先尝试该返回结果是不是被异步请求的
            MessageResponseHandler messageResponseHandler = BlasterSender.responseHandlerMap.get(id);
            RequestWrapper originRequest = BlasterSender.originRequestMap.get(id);
            if(messageResponseHandler != null && originRequest != null) {
                try {
                    if(log.isDebugEnabled()) {
                        log.debug("接收到同步响应消息,运行回调函数,requestId[{}], response[{}]", protocol.getId(), response);
                    }
                    MessageResponseDispatcher.handleResponse(originRequest, response, messageResponseHandler);
                } catch (Exception e) {
                    log.error("进行异步结果处理的时候发生错误", e);
                } finally {
                    //clean
                    BlasterSender.originRequestMap.remove(id);
                    BlasterSender.responseHandlerMap.remove(id);
                }
            }

            //尝试是不是同步请求的
            TransferQueue<ResponseWrapper> requestWaiterQueue = BlasterSender.requestWaiterQueueMap.get(id);
            if(requestWaiterQueue != null && requestWaiterQueue.hasWaitingConsumer()) {
                //如果队列不为null并且有消费者在等待,尝试将响应结果传给等待线程
                boolean transferResponseToWaitingThreadSuccess = requestWaiterQueue.tryTransfer(response);
                if(log.isDebugEnabled()) {
                    log.debug("接收到异步响应消息,尝试将消息放入队列,requestId[{}], success[{}]", protocol.getId(), transferResponseToWaitingThreadSuccess);
                }
                if(transferResponseToWaitingThreadSuccess)  {
                    //成功将响应结果传给等待线程
                    return null;
                }
            }

            //此时该响应结果不知道如何处理,记录警告信息
            log.warn("接收到无处理器的响应信息:" + response.toString());
            return null;
        }


    }


}
