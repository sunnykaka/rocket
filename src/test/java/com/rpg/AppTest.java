package com.rpg;


import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Unit test for simple App.
 */
@Test
public class AppTest {

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
