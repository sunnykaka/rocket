package com.rpg.rocket.blaster.registry;

import com.google.common.base.Preconditions;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import com.rpg.rocket.blaster.MessageRequestHandler;
import com.rpg.rocket.blaster.MessageResponseHandler;
import com.rpg.rocket.exception.BlasterException;
import com.rpg.rocket.exception.RocketException;
import com.rpg.rocket.message.LoginProtos;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * User: liubin
 * Date: 14-2-27
 */
public class MessageHandlerRegistry {

    private static final MessageHandlerRegistry instance = new MessageHandlerRegistry();
    private MessageHandlerRegistry() {
    }
    public static final MessageHandlerRegistry getInstance() {
        return instance;
    }

    private AtomicBoolean initialized = new AtomicBoolean(false);

    private Map<String, MessageRequestHandler> requestHandlerMap = new HashMap<>();
    private Map<String, MessageResponseHandler> responseHandlerMap = new HashMap<>();

    public void init() {
        if(initialized.compareAndSet(false, true)) {

        }
    }

    public void registerMessageRequestHandler(Descriptors.Descriptor descriptor, MessageRequestHandler requestHandler) {
        String messageType = descriptor.getFullName();
        if(requestHandlerMap.containsKey(messageType)) {
            throw new BlasterException(String.format("注册messageRequestHandler的时候发生错误,messageType[%s]对应的handler已经存在", messageType));
        }
        requestHandlerMap.put(messageType, requestHandler);
    }

    public void registerMessageResponseHandler(Descriptors.Descriptor descriptor, MessageResponseHandler responseHandler) {
        String messageType = descriptor.getFullName();
        if(responseHandlerMap.containsKey(messageType)) {
            throw new BlasterException(String.format("注册messageResponseHandler的时候发生错误,messageType[%s]对应的handler已经存在", messageType));
        }
        responseHandlerMap.put(messageType, responseHandler);
    }

    public MessageRequestHandler getMessageRequestHandler(String messageType) {
        if(messageType == null) return null;
        return requestHandlerMap.get(messageType);
    }

    public MessageResponseHandler getMessageResponseHandler(String messageType) {
        if(messageType == null) return null;
        return responseHandlerMap.get(messageType);
    }

}
