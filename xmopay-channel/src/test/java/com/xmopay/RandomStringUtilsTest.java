package com.xmopay;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

/**
 * com.xmopay
 *
 * @author echo_coco.
 * @date 6:16 PM, 2018/5/2
 */
public class RandomStringUtilsTest {

    @Test
    public void testRandomString() {
        System.out.println(RandomStringUtils.random(16));
        System.out.println(RandomStringUtils.random(16, "abcde12345"));
        System.out.println(RandomStringUtils.randomAlphanumeric(16));
        System.out.println(RandomStringUtils.randomAlphabetic(16));
        System.out.println(RandomStringUtils.randomAscii(16));
        System.out.println(RandomStringUtils.randomGraph(16));
        System.out.println(RandomStringUtils.randomNumeric(16));
    }
}
