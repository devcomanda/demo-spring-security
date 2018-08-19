package com.devcomanda.demospringsecurity.utils;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
public final class RandomUtil {
    private RandomUtil() {}

    private static final int NAME_COUNT = 10;

    private static final int ACTIVATION_KEY_COUNT = 20;

    private static final int PASS_COUNT = 7;

    public static String generateName() {
        return RandomStringUtils.randomAlphanumeric(RandomUtil.NAME_COUNT);
    }

    public static String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(RandomUtil.PASS_COUNT);
    }

    public static String generateActivationKey() {
        return RandomStringUtils.randomNumeric(RandomUtil.ACTIVATION_KEY_COUNT);
    }

}
