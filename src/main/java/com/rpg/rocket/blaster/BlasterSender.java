package com.rpg.rocket.blaster;

import com.google.protobuf.Message;
import com.rpg.rocket.blaster.registry.MessageHandlerRegistry;
import com.rpg.rocket.exception.RocketProtocolException;
import com.rpg.rocket.pb.DescriptorRegistry;
import com.rpg.rocket.protocol.RocketProtocol;
import com.rpg.rocket.util.Clock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * User: liubin
 * Date: 14-3-3
 */
public class BlasterSender {

    private static final Logger log = LoggerFactory.getLogger(BlasterSender.class);

    private DescriptorRegistry descriptorRegistry = DescriptorRegistry.getInstance();
    private MessageHandlerRegistry messageHandlerRegistry = MessageHandlerRegistry.getInstance();


}
