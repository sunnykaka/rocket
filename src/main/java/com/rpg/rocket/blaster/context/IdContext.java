package com.rpg.rocket.blaster.context;

import com.rpg.rocket.blaster.message.MessageResponseHandler;
import com.rpg.rocket.blaster.protocol.RequestWrapper;
import com.rpg.rocket.blaster.protocol.ResponseWrapper;

import java.util.concurrent.TransferQueue;

/**
 * id上下文对象,用于存储请求之前需要保存的和id有关的信息,供接收消息的时候取用
 * User: liubin
 * Date: 14-3-16
 */
public class IdContext {

    //请求id
    private int id;

    //是否是异步请求
    private boolean async;

    //原始请求消息对象
    private RequestWrapper originRequest;

    //注册的响应回调函数
    private MessageResponseHandler messageResponseHandler;

    //同步请求会用到的请求阻塞队列,有响应消息的时候就放入这个队列里
    private TransferQueue<ResponseWrapper> requestWaiterQueue;

    public IdContext(int id, boolean async, RequestWrapper originRequest, MessageResponseHandler messageResponseHandler, TransferQueue<ResponseWrapper> requestWaiterQueue) {
        this.id = id;
        this.async = async;
        this.originRequest = originRequest;
        this.messageResponseHandler = messageResponseHandler;
        this.requestWaiterQueue = requestWaiterQueue;
    }

    public int getId() {
        return id;
    }

    public boolean isAsync() {
        return async;
    }

    public MessageResponseHandler getMessageResponseHandler() {
        return messageResponseHandler;
    }

    public RequestWrapper getOriginRequest() {
        return originRequest;
    }

    public TransferQueue<ResponseWrapper> getRequestWaiterQueue() {
        return requestWaiterQueue;
    }
}
