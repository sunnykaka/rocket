package com.rpg.rocket.pb;

import com.google.protobuf.Descriptors;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * User: liubin
 * Date: 14-2-27
 */
public class DescriptorRegistry {

    private static final DescriptorRegistry instance = new DescriptorRegistry();
    private DescriptorRegistry() {}
    public static final DescriptorRegistry getInstance() {
        return instance;
    }

    private AtomicBoolean initialized = new AtomicBoolean(false);

    private Map<String, Descriptors.Descriptor> descriptorMap = new HashMap<>();

    public void init() {
        if(initialized.compareAndSet(false, true)) {

        }
    }

    public Descriptors.Descriptor getDesciptor(String fullName) {
        return descriptorMap.get(fullName);
    }


}
