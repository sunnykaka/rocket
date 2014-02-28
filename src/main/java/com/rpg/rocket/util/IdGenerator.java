package com.rpg.rocket.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * User: liubin
 * Date: 14-2-28
 */
public class IdGenerator {

    private static AtomicInteger requestId = new AtomicInteger(0);


    public static int getRequestId() {
        return requestId.incrementAndGet();
    }

}
