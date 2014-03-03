package com.rpg.rocket.pb;

import com.google.common.base.Preconditions;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
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

    private Map<String, Class<? extends Message>> pbNameToClassMap = new HashMap<>();
    private Map<String, Method> pbNameToParseMethodMap = new HashMap<>();

    private void init() {
        if(initialized.compareAndSet(false, true)) {
            instance.register(LoginProtos.class);
        }
    }

    public Class<? extends Message> getMessageClass(String fullName) {
        return pbNameToClassMap.get(fullName);
    }

    public Method getMessageParseMethod(String fullName) {
        return pbNameToParseMethodMap.get(fullName);
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
            pbNameToClassMap.put(descriptor.getFullName(), (Class<? extends Message>)clazz);

        } else if(getDescriptorResult instanceof Descriptors.FileDescriptor) {
            Descriptors.FileDescriptor fileDescriptor = (Descriptors.FileDescriptor)getDescriptorResult;
            for(Descriptors.Descriptor descriptor : fileDescriptor.getMessageTypes()) {
                String className = fileDescriptor.getOptions().getJavaPackage() + "." + fileDescriptor.getOptions().getJavaOuterClassname()
                        + "$" + descriptor.getName();
                Class<? extends Message> messageClass;
                try {
                    messageClass = (Class<? extends Message>) Class.forName(className);
                    pbNameToClassMap.put(descriptor.getFullName(), messageClass);
                } catch (ClassNotFoundException e) {
                    throw new RocketException(String.format("根据messageClassName找不到对应的class,register class[%s], getDescriptorResultClass[%s], messageClassName[%s]",
                            clazz.getName(), getDescriptorResult.getClass().getName(), className));
                }
                try {
                    pbNameToParseMethodMap.put(descriptor.getFullName(), messageClass.getMethod("parseFrom", byte[].class));
                } catch (NoSuchMethodException e) {
                    throw new RocketException(String.format("messageClass没有parseFrom方法,register class[%s], getDescriptorResultClass[%s], messageClassName[%s]",
                            clazz.getName(), getDescriptorResult.getClass().getName(), className));
                }
            }
        } else {
            throw new RocketException(String.format("调用getDescriptor后得到未知的Descriptor类型,class[%s], getDescriptorResultClass[%s]",
                    clazz.getName(), getDescriptorResult.getClass().getName()));
        }
    }

}
