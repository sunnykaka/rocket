package com.rpg.rocket.blaster;

import com.google.protobuf.Message;
import com.rpg.rocket.blaster.registry.MessageHandlerRegistry;
import com.rpg.rocket.exception.AppException;
import com.rpg.rocket.exception.RocketProtocolException;
import com.rpg.rocket.message.BaseMsgProtos;
import com.rpg.rocket.pb.DescriptorRegistry;
import com.rpg.rocket.protocol.RequestWrapper;
import com.rpg.rocket.protocol.ResponseWrapper;
import com.rpg.rocket.protocol.RocketProtocol;
import com.rpg.rocket.util.Clock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * User: liubin
 * Date: 14-3-3
 */
public class BlasterReceiver {

    private static final Logger log = LoggerFactory.getLogger(BlasterReceiver.class);


    private MessageHandlerRegistry messageHandlerRegistry = MessageHandlerRegistry.getInstance();

    public ResponseWrapper receive(RocketProtocol protocol) throws RocketProtocolException {

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

            long timeout = protocol.getTimeout();
            if(Clock.isTimeout(timeout)) {
                return null;
            }

            RequestWrapper request = new RequestWrapper(protocol);
            BaseMsgProtos.RequestMsg requestMsg = request.getRequestMsg();
            Message message = request.getMessage();

            MessageRequestHandler messageRequestHandler = messageHandlerRegistry.getMessageRequestHandler(requestMsg.getMessageType());
            if(messageRequestHandler == null) {
                log.warn("接收到类型为[{}]的消息,但是没有对应的请求处理器,消息内容:{}", requestMsg.getMessageType(), protocol.toString());
                return null;
            }

            Message result = null;
            boolean exceptionCaught = false;
            BaseMsgProtos.ResponseStatus responseStatus = BaseMsgProtos.ResponseStatus.UNKNOWN_ERROR;
            String responseMsg = null;

            try {
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

            return new ResponseWrapper(1, messageRequestHandler.getPhase(), id, RocketProtocol.Status.SUCCESS, responseStatus, responseMsg, result);

        } else {


            ResponseWrapper response = new ResponseWrapper(protocol);
            RocketProtocol.Status status = protocol.getStatus();

            return null;
        }


    }


}
