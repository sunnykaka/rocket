package com.rpg;


import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Unit test for simple App.
 */
@Test
public class AppTest
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    @BeforeTest
    public void init()
    {
        System.out.println("before");
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertThat(true, is(true));
    }
}
