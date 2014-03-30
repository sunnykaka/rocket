package com.rpg.rocket;


import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Unit test for simple App.
 */
@Test
public class PlainTest {

    @BeforeTest
    public void init() {
        System.out.println("before");
    }

    @Test(enabled = false)
    public void testApp() {
        print1(13);
    }

    @Test
    public void test2() {
        long l = 2147483649L;
        int i = (int)l;
        System.out.println(i);
        long l2 = i & 0xFFFFFFFFL;
        System.out.println(l2);
    }

    @Test
    public void test3() {
        System.out.println("native order: " + ByteOrder.nativeOrder());

        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        System.out.println("byteBuffer order: " + byteBuffer.order());
        byteBuffer.putShort((short)1);
        System.out.println("change order: " + ByteOrder.LITTLE_ENDIAN);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putShort((short) 1);
        byte[] array = byteBuffer.array();
        System.out.println(array.length);
        for (int i = 0; i < array.length; i++) {
            System.out.print(array[i] + ", ");
        }

    }

    private void print1(int num) {
        byte[] array = new byte[32];
        for (int i = 31; i >= 0; i--) {
            array[31 - i] = (byte)((num >>> i) & 0x00000001);
        }
        String s = "[";
        for (int i = 0; i < array.length; i++) {
            s += array[i];
            if(i != array.length - 1) {
                s += ", ";
            }
        }
        s += "]";
        System.out.println(s);
//        System.out.println(Integer.toBinaryString(num));
    }

    private void print2 (int num) {
        byte[] array = new byte[32];
        for (int i = 31; i >= 0; i--) {
            array[i] = (byte)(num & 1);
            num = num >> 1;
        }
        String s = "[";
        for (int i = 0; i < array.length; i++) {
            s += array[i];
            if(i != array.length - 1) {
                s += ", ";
            }
        }
        s += "]";
        System.out.println(s);
    }
}
