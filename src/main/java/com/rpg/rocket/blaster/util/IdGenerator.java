package com.rpg.rocket.blaster.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * User: liubin
 * Date: 14-2-28
 */
public class IdGenerator {

    private static AtomicInteger requestId = new AtomicInteger(0);

    /**
     * 生成请求id
     * @return
     */
    public static int getRequestId() {
        //TODO 解决int值会溢出的问题
        return requestId.incrementAndGet();
    }

}
