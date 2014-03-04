package com.rpg.rocket.blaster;

import com.google.protobuf.Message;

/**
 * User: liubin
 * Date: 14-3-4
 */
public interface MessageRequestHandler {

    Message handleRequest(Message message);

}
