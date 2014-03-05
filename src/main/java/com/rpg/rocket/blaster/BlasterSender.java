package com.rpg.rocket.blaster;

import com.google.protobuf.Message;
import com.rpg.rocket.blaster.registry.MessageHandlerRegistry;
import com.rpg.rocket.exception.RocketProtocolException;
import com.rpg.rocket.message.BaseMsgProtos;
import com.rpg.rocket.pb.DescriptorRegistry;
import com.rpg.rocket.protocol.RequestWrapper;
import com.rpg.rocket.protocol.ResponseWrapper;
import com.rpg.rocket.protocol.RocketProtocol;
import com.rpg.rocket.util.Clock;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;

/**
 * User: liubin
 * Date: 14-3-3
 */
public class BlasterSender {

    private static final Logger log = LoggerFactory.getLogger(BlasterSender.class);

    private DescriptorRegistry descriptorRegistry = DescriptorRegistry.getInstance();
    private MessageHandlerRegistry messageHandlerRegistry = MessageHandlerRegistry.getInstance();

    private static Map<Integer, TransferQueue<ResponseWrapper>> requestWaiterQueueMap = new ConcurrentHashMap<>();

    private static Map<Integer, MessageResponseHandler> responseHandlerMap = new ConcurrentHashMap<>();

    private static Map<Integer, RequestWrapper> originRequestMap = new ConcurrentHashMap<>();

    public void sendResponse(Channel channel, ResponseWrapper response) {
        RocketProtocol protocol = response.getProtocol();
        if(protocol.getId() <= 0) {
            //id无效,无需返回结果
            log.warn("接收到id小于1的消息,id[{}],无法返回消息,消息内容:{}", protocol.getId(), protocol.toString());
            return;
        }
        if(Clock.isTimeout(protocol.getTimeout())) {
            //已超时,无需返回结果
            return;
        }
        channel.write(protocol);
    }

    public void sendRequest(Channel channel, RequestWrapper request, final boolean async, MessageResponseHandler messageResponseHandler) {

        channel.write(request.getRequestMsg()).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if(future.isSuccess()) {
                    return;
                }
                if(!async) {
                    //同步调用的写消息不成功,就往队列里放错误消息
//                    queueMap.get(protocol.getId).put(...);
                } else {
                    //异步调用的写消息不成功,就执行错误处理方法
                    //executor.execute(responseHandlerMap.get(protocol.getId));
                }
            }
        });

        if(messageResponseHandler == null) return;

        if(!async) {

            TransferQueue<ResponseWrapper> requestWaiterQueue = new LinkedTransferQueue<>();
            requestWaiterQueueMap.put(request.getProtocol().getId(), requestWaiterQueue);

            long timeoutInMillseconds = request.getProtocol().getTimeout() - Clock.nowInMillisecond();
            ResponseWrapper response = null;
            if(timeoutInMillseconds > 0) {
                try {
                    response = requestWaiterQueue.poll(timeoutInMillseconds, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    log.error("", e);
                }
            } else {
                response = requestWaiterQueue.poll();
            }
            RocketProtocol.Status status = RocketProtocol.Status.TIMEOUT;
            if(response != null) {
                status = response.getProtocol().getStatus();
            }

            if(RocketProtocol.Status.SUCCESS.equals(status)) {
                messageResponseHandler.handleResponse(request.getRequestInfo(), request.getMessage(), )
            } else {

            }



//            requestWaiterQueue.take()
//            queueMap.put(protocol.getId, queue);
//            RocketProtocol response = queue.take(timeout);
            //判断response成功与否,如果不成功,调用错误处理方法,否则调用处理方法
            //最后将返回值返回
            //return handler.handleReponse(message);
        } else {
            responseHandlerMap.put(protocol.getId, handler);
            return null;
        }

    }


}
