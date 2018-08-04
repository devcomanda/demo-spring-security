package com.devcomanda.demospringsecurity.utils;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
public final class RandomUtil {
    private static final int NAME_COUNT = 10;
    private static final int CODE_COUNT = 3;

    private RandomUtil() {}


    public static String generateName() {
        return RandomStringUtils.randomAlphanumeric(RandomUtil.NAME_COUNT);
    }


    public static String generateCode() {
        return RandomStringUtils.randomAlphanumeric(RandomUtil.CODE_COUNT);
    }
}
