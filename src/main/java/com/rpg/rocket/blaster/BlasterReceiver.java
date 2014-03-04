package com.rpg.rocket.blaster;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import com.rpg.rocket.blaster.registry.MessageHandlerRegistry;
import com.rpg.rocket.exception.RocketProtocolException;
import com.rpg.rocket.pb.DescriptorRegistry;
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

    private DescriptorRegistry descriptorRegistry = DescriptorRegistry.getInstance();
    private MessageHandlerRegistry messageHandlerRegistry = MessageHandlerRegistry.getInstance();

    public void receive(RocketProtocol protocol) throws RocketProtocolException {

//        boolean decipher = false;
//
//        if(protocol.getVersion() != 1) {
//            throw new RocketProtocolException(RocketProtocol.Status.INVALID_VERSION, protocol);
//        }
//
//        if(RocketProtocol.Phase.PLAINTEXT.equals(protocol.getPhase())) {
//
//        } else if(RocketProtocol.Phase.CIPHERTEXT.equals(protocol.getPhase())) {
//            decipher = true;
//        } else {
//            throw new RocketProtocolException(RocketProtocol.Status.INVALID_PHASE, protocol);
//        }
//
//        int id = protocol.getId();
//
//        if(decipher) {
//            //TODO decipher
//        }
//
//        Method messageParseMethod = descriptorRegistry.getMessageParseMethod(protocol.getMessageType());
//        if(messageParseMethod == null) {
//            throw new RocketProtocolException(RocketProtocol.Status.UNKNOWN_MESSAGE_TYPE, protocol);
//        }
//        Message message;
//        try {
//            message = (Message)messageParseMethod.invoke(null, protocol.getData());
//        } catch (IllegalAccessException | InvocationTargetException e) {
//            throw new RocketProtocolException(RocketProtocol.Status.DATA_CORRUPT, protocol);
//        }
//
//        if(RocketProtocol.Type.REQUEST.equals(protocol.getType())) {
//            long timeout = protocol.getTimeout();
//            if(Clock.isTimeout(timeout)) {
//                // blasterSender.send(timeout);
//                return;
//            }
//
//            MessageRequestHandler messageRequestHandler = messageHandlerRegistry.getMessageRequestHandler(protocol.getMessageType());
//            if(messageRequestHandler == null) {
//                log.warn("接收到类型为[{}]的消息,但是没有对应的请求处理器,消息内容:{}", protocol.getMessageType(), protocol.toString());
//                return;
//            }
//
//            Message result = messageRequestHandler.handleRequest(message);
//            if(result == null) return;
//
//
//            // blasterSender.send(result);
//
//
//        } else {
//            RocketProtocol.Status status = protocol.getStatus();
//
//
//        }


    }


}
