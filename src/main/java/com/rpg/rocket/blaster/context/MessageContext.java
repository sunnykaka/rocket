package com.rpg.rocket.blaster.context;

import com.rpg.rocket.blaster.message.MessageResponseHandler;
import com.rpg.rocket.blaster.protocol.RequestWrapper;
import com.rpg.rocket.blaster.protocol.ResponseWrapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;

/**
 * 消息上下文对象,用于取得跟id有关的上下文
 * User: liubin
 * Date: 14-3-16
 */
public class MessageContext {

    private static final MessageContext INSTANCE = new MessageContext();
    private MessageContext() {}
    public static final MessageContext getInstance() {
        return INSTANCE;
    }

    private Map<Integer, IdContext> idContextMap = new ConcurrentHashMap<>();

    /**
     * 初始化id上下文
     * @param id
     * @param async
     * @param originRequest
     * @param messageResponseHandler
     * @return
     */
    public IdContext initContext(int id, boolean async, RequestWrapper originRequest, MessageResponseHandler messageResponseHandler) {
        TransferQueue<ResponseWrapper> requestWaiterQueue = new LinkedTransferQueue<>();
        IdContext idContext = new IdContext(id, async, originRequest, messageResponseHandler, requestWaiterQueue);
        idContextMap.put(id, idContext);
        return idContext;
    }

    public IdContext getContext(int id) {
        return idContextMap.get(id);
    }

    public void removeContext(int id) {
        idContextMap.remove(id);
    }
}
