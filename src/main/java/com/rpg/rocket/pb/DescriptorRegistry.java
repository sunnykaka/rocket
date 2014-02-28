package com.rpg.rocket.pb;

import com.google.common.base.Preconditions;
import com.google.protobuf.Descriptors;
import com.rpg.rocket.domain.UserProtos;
import com.rpg.rocket.exception.RocketException;
import com.rpg.rocket.message.LoginProtos;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * User: liubin
 * Date: 14-2-27
 */
public class DescriptorRegistry {

    private static final DescriptorRegistry instance = new DescriptorRegistry();
    private DescriptorRegistry() {
        init();
    }
    public static final DescriptorRegistry getInstance() {
        return instance;
    }

    private AtomicBoolean initialized = new AtomicBoolean(false);

    private Map<String, Descriptors.Descriptor> descriptorMap = new HashMap<>();

    private void init() {
        if(initialized.compareAndSet(false, true)) {
            instance.register(LoginProtos.class);
        }
    }

    public Descriptors.Descriptor getDesciptor(String fullName) {
        return descriptorMap.get(fullName);
    }

    public void register(Class<?> clazz) {
        Object getDescriptorResult;
        try {
            getDescriptorResult = clazz.getMethod("getDescriptor").invoke(null);
        } catch (NoSuchMethodException e) {
            throw new RocketException(String.format("class[%s]没有getDescriptor这个类方法,传进来的是protobuf生成类?", clazz.getName()), e);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RocketException(e);
        }
        if(getDescriptorResult instanceof Descriptors.Descriptor) {
            Descriptors.Descriptor descriptor = (Descriptors.Descriptor)getDescriptorResult;
            descriptorMap.put(descriptor.getFullName(), descriptor);

        } else if(getDescriptorResult instanceof Descriptors.FileDescriptor) {
            Descriptors.FileDescriptor fileDescriptor = (Descriptors.FileDescriptor)getDescriptorResult;
            for(Descriptors.Descriptor descriptor : fileDescriptor.getMessageTypes()) {
                descriptorMap.put(descriptor.getFullName(), descriptor);
            }
        } else {
            throw new RocketException(String.format("调用getDescriptor后得到未知类型,class[%s], getDescriptorResultClass[%s]",
                    clazz.getName(), getDescriptorResult.getClass().getName()));
        }
    }

}
