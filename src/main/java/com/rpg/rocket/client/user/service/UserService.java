package com.rpg.rocket.client.user.service;

import com.rpg.rocket.blaster.message.MessageDispatcher;
import com.rpg.rocket.client.RocketClient;
import com.rpg.rocket.domain.UserProtos;

/**
 * User: liubin
 * Date: 14-7-1
 */
public class UserService {

    private RocketClient rocketClient;

    public UserService(RocketClient rocketClient) {
        this.rocketClient = rocketClient;
    }

    public UserProtos.User login(String username, String password) {
        MessageDispatcher messageDispatcher = MessageDispatcher.getInstance();
//        messageDispatcher.request();
        return null;

    }


}
