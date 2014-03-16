package com.rpg.rocket.blaster.protocol;

/**
 * User: liubin
 * Date: 14-3-5
 */
public class RequestInfo {

    private Long userId;

    public RequestInfo(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }
}
