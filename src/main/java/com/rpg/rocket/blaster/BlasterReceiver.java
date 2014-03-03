package com.rpg.rocket.blaster;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import com.rpg.rocket.exception.RocketProtocolException;
import com.rpg.rocket.pb.DescriptorRegistry;
import com.rpg.rocket.protocol.RocketProtocol;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * User: liubin
 * Date: 14-3-3
 */
public class BlasterReceiver {

    private DescriptorRegistry descriptorRegistry = DescriptorRegistry.getInstance();

    public void receive(RocketProtocol protocol) throws RocketProtocolException {

        boolean decipher = false;

        if(protocol.getVersion() != 1) {
            throw new RocketProtocolException(RocketProtocol.Status.INVALID_VERSION, protocol);
        }

        if(RocketProtocol.Phase.PLAINTEXT.equals(protocol.getPhase())) {

        } else if(RocketProtocol.Phase.CIPHERTEXT.equals(protocol.getPhase())) {
            decipher = true;
        } else {
            throw new RocketProtocolException(RocketProtocol.Status.INVALID_PHASE, protocol);
        }

        int id = protocol.getId();

        if(decipher) {
            //TODO decipher
        }

        Method messageParseMethod = descriptorRegistry.getMessageParseMethod(protocol.getMessageType());
        if(messageParseMethod == null) {
            throw new RocketProtocolException(RocketProtocol.Status.UNKNOWN_MESSAGE_TYPE, protocol);
        }
        Message message = null;
        try {
            message = (Message)messageParseMethod.invoke(null, protocol.getData());
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RocketProtocolException(RocketProtocol.Status.DATA_CORRUPT, protocol);
        }

        if(RocketProtocol.Type.REQUEST.equals(protocol.getType())) {
            long timeout = protocol.getTimeout();



        } else {
            RocketProtocol.Status status = protocol.getStatus();


        }


    }


}
