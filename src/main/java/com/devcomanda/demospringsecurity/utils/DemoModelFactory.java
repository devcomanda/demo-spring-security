package com.devcomanda.demospringsecurity.utils;

import com.devcomanda.demospringsecurity.model.DemoModel;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
public final class DemoModelFactory {
    private DemoModelFactory() {}

    public static DemoModel createRandomDemoModel(int id) {

        return new DemoModel(
                id,
                RandomUtil.generateName(),
                RandomUtil.generateName()
        );
    }
}
