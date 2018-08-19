package com.devcomanda.demospringsecurity.factories;

import com.devcomanda.demospringsecurity.config.constants.AuthoritiesConstants;
import com.devcomanda.demospringsecurity.model.Authority;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
public final class AuthoritiesFactory {
    private AuthoritiesFactory() {
    }

    public static final Authority ADMIN_AUTHORITY = new Authority(AuthoritiesConstants.ROLE_ADMIN);
    public static final Authority USER_AUTHORITY = new Authority(AuthoritiesConstants.ROLE_USER);
}
