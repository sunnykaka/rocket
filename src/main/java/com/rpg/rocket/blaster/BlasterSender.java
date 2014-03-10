package com.rpg.rocket.blaster;

import com.rpg.rocket.blaster.registry.MessageHandlerRegistry;
import com.rpg.rocket.common.SysConstants;
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

    public static Map<Integer, TransferQueue<ResponseWrapper>> requestWaiterQueueMap = new ConcurrentHashMap<>();

    public static Map<Integer, MessageResponseHandler> responseHandlerMap = new ConcurrentHashMap<>();

    public static Map<Integer, RequestWrapper> originRequestMap = new ConcurrentHashMap<>();

    public void sendResponse(Channel channel, ResponseWrapper response) {
        RocketProtocol protocol = response.getProtocol();
        if(protocol.getId() <= 0) {
            //id无效,无需返回结果
            log.warn("接收到id小于1的消息,id[{}],无法返回消息,消息内容:{}", protocol.getId(), response.toString());
            return;
        }
        if(log.isDebugEnabled()) {
            log.debug("准备发送响应, id[{}], response[{}]", new Object[]{protocol.getId(), response});
        }
        if(Clock.isTimeout(protocol.getTimeout())) {
            //已超时,无需返回结果
            return;
        }
        channel.write(protocol);
    }

    public void sendRequest(Channel channel, RequestWrapper request, final boolean async, MessageResponseHandler messageResponseHandler) {

        final int id = request.getProtocol().getId();

        if(log.isDebugEnabled()) {
            log.debug("准备发送请求, id[{}], request[{}], async[{}], messageResponseHandler[{}]", new Object[]{id, request, async, messageResponseHandler});
        }

        channel.write(request.getRequestMsg()).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if(log.isDebugEnabled()) {
                    log.debug("发送请求结束, id[{}], async[{}], success[{}]", new Object[]{id, async, future.isSuccess()});
                }
                if(future.isSuccess()) {
                    return;
                }
                if(!async) {
                    //同步调用的写消息不成功,就往队列里放错误消息
                    TransferQueue<ResponseWrapper> requestWaiterQueue = requestWaiterQueueMap.get(id);
                    if(requestWaiterQueue != null) {
                        ResponseWrapper response = createSendRequestFailedResponse(id);
                        try {
                            requestWaiterQueue.tryTransfer(response, 1000, TimeUnit.MILLISECONDS);
                        } catch (InterruptedException ignore) {}
                    }
                } else {
                    //异步调用的写消息不成功,就执行错误处理方法
                    //executor.execute(responseHandlerMap.get(protocol.getId));
                    RequestWrapper originRequest = originRequestMap.get(id);
                    MessageResponseHandler messageResponseHandler = responseHandlerMap.get(id);
                    if(originRequest == null || messageResponseHandler == null) return;

                    try {
                        ResponseWrapper response = createSendRequestFailedResponse(id);
                        MessageResponseDispatcher.handleResponse(originRequest, response, messageResponseHandler);
                    } catch (Exception e) {
                        log.error("进行异步结果处理的时候发生错误", e);
                    } finally {
                        //clean
                        originRequestMap.remove(id);
                        responseHandlerMap.remove(id);
                    }

                }
            }
        });

        if(messageResponseHandler == null) return;

        if(!async) {

            TransferQueue<ResponseWrapper> requestWaiterQueue = new LinkedTransferQueue<>();
            requestWaiterQueueMap.put(id, requestWaiterQueue);

            try {
                long timeoutInMillseconds = request.getProtocol().getTimeout() - Clock.nowInMillisecond();
                ResponseWrapper response = null;
                if(timeoutInMillseconds > 0) {
                    try {
                        log.debug("同步请求开始等待返回结果,id[{}],等待时间[{}ms]", id, timeoutInMillseconds);
                        response = requestWaiterQueue.poll(timeoutInMillseconds, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                        log.error("", e);
                    }
                } else {
                    response = requestWaiterQueue.poll();
                }
                MessageResponseDispatcher.handleResponse(request, response, messageResponseHandler);
            } catch (Exception e) {
                log.error("同步发送消息并且进行结果处理的时候发生错误", e);
            } finally {
                //clean
                requestWaiterQueueMap.remove(id);
            }

        } else {

            originRequestMap.put(id, request);
            responseHandlerMap.put(id, messageResponseHandler);

            //TODO 需要对异步请求的超时情况处理,将超时的回调函数从map中取出,并执行超时的回调方法

        }

    }

    private ResponseWrapper createSendRequestFailedResponse(int id) {
        return new ResponseWrapper(SysConstants.PROTOCOL_VERSION, RocketProtocol.Phase.PLAINTEXT, id,
                RocketProtocol.Status.REQUEST_FAILED, null, null, null);
    }



}
