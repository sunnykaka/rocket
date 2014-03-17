package com.rpg.rocket.blaster.registry;

import com.google.protobuf.Descriptors;
import com.rpg.rocket.blaster.message.MessageRequestHandler;
import com.rpg.rocket.blaster.message.MessageResponseHandler;
import com.rpg.rocket.blaster.exception.BlasterException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 消息处理注册器
 *
 * 无论服务器还是客户端都可以对某个消息的到达注册回调函数,消息到的时候会自动调用该函数进行处理
 *
 *
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

    /**
     * 注册消息请求回调函数
     * @param descriptor
     * @param requestHandler
     */
    public void registerMessageRequestHandler(Descriptors.Descriptor descriptor, MessageRequestHandler requestHandler) {
        String messageType = descriptor.getFullName();
        if(requestHandlerMap.containsKey(messageType)) {
            throw new BlasterException(String.format("注册messageRequestHandler的时候发生错误,messageType[%s]对应的handler已经存在", messageType));
        }
        requestHandlerMap.put(messageType, requestHandler);
    }

    public MessageRequestHandler deregisterMessageRequestHandler(Descriptors.Descriptor descriptor) {
        return requestHandlerMap.remove(descriptor.getFullName());
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
