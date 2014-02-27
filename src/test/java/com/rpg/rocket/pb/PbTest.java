package com.rpg.rocket.pb;


import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.rpg.rocket.domain.UserProtos;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Unit test for simple App.
 */
@Test
public class PbTest {

    @BeforeTest
    public void init() {
        System.out.println("before");
    }

    @Test
    public void test() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Map<String, String> descriptorNameMap = new HashMap<>();
        String descriptorName = UserProtos.User.getDescriptor().getFullName();
        descriptorNameMap.put(descriptorName, UserProtos.User.class.getName());

        System.out.println(descriptorName);

        String className = descriptorNameMap.get(descriptorName);
        Descriptors.Descriptor descriptor = (Descriptors.Descriptor) Class.forName(className).getMethod("getDescriptor").invoke(null);

        assertThat(descriptor, notNullValue());
        assertThat(descriptor, is(UserProtos.User.getDescriptor()));





//        DescriptorProtos.FileDescriptorSet descriptor1 = (DescriptorProtos.FileDescriptorSet)DescriptorProtos.getDescriptor().getMessageTypes().get(0);
//        for(Descriptors.Descriptor descriptor : DescriptorProtos.getDescriptor().getMessageTypes()) {
//            System.out.println(descriptor.getFullName());
//        }

//        Descriptors.Descriptor descriptor = DescriptorProtos.getDescriptor().findMessageTypeByName(messageTypeName);
//        assertThat(descriptor, notNullValue());
//        assertThat(descriptor, is(UserProtos.User.getDescriptor()));
    }

}
