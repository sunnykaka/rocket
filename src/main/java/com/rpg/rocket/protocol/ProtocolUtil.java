package com.rpg.rocket.protocol;

import com.google.protobuf.Message;
import com.rpg.rocket.exception.RocketProtocolException;
import com.rpg.rocket.pb.DescriptorRegistry;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * User: liubin
 * Date: 14-3-5
 */
public class ProtocolUtil {

    private static final DescriptorRegistry descriptorRegistry = DescriptorRegistry.getInstance();

    public static Message parseMessageFromDataAndType(String messageType, byte[] messageBytes) {
        Method messageParseMethod = descriptorRegistry.getMessageParseMethod(messageType);
        if(messageParseMethod == null) {
            throw new RocketProtocolException(RocketProtocol.Status.UNKNOWN_MESSAGE_TYPE, null);
        }
        try {
            Message message = (Message)messageParseMethod.invoke(null, messageBytes);
            return message;
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RocketProtocolException(RocketProtocol.Status.DATA_CORRUPT, null);
        }
    }

}
