package com.devcomanda.demospringsecurity.factories;


import com.devcomanda.demospringsecurity.model.User;
import com.devcomanda.demospringsecurity.model.projections.ReadableUser;
import com.devcomanda.demospringsecurity.model.projections.UpdatableUser;
import com.devcomanda.demospringsecurity.web.api.requests.NewUserReq;
import com.devcomanda.demospringsecurity.web.api.requests.UpdateUserReq;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
public final class UsersFactory {
    private UsersFactory() {
    }

    public static final Long USER_ID = 1L;

    public static final String USER_PASSWORD = "user";
    public static final String UPDATED_USER_PASSWORD = "user2updated";

    public static final String USER_EMAIL = "system@localhost";
    public static final String UPDATED_USER_EMAIL = "updateUser@localhost";

    public static final String USER_FIRSTNAME = "System";
    public static final String UPDATED_USER_FIRSTNAME = "updateUserFirstName";

    public static final String USER_LASTNAME = "System";
    public static final String UPDATED_USER_LASTNAME = "updateUserLastName";

    public static final String USER_ACTIVATION_KEY = "activation-key";

    public static User createActivatedUserEntity() {
        return UsersFactory.createUser(
                UsersFactory.USER_ID,
                UsersFactory.USER_EMAIL,
                UsersFactory.USER_FIRSTNAME,
                UsersFactory.USER_LASTNAME,
                true
        );
    }

    public static UpdatableUser createUpdatableUser() {
        return new UpdatableUser(
                UsersFactory.USER_ID,
                UsersFactory.USER_FIRSTNAME,
                UsersFactory.USER_LASTNAME,
                UsersFactory.USER_EMAIL);
    }

    public static User createNotActivatedUserEntity() {
        final User user = UsersFactory.createUser(
                UsersFactory.USER_ID,
                UsersFactory.USER_EMAIL,
                UsersFactory.USER_FIRSTNAME,
                UsersFactory.USER_LASTNAME,
                false
        );
        user.setActivationKey(UsersFactory.USER_ACTIVATION_KEY);
        return user;
    }

    public static User createRandomUserEntity() {
        final User user = UsersFactory.createActivatedUserEntity();
        user.setEmail(RandomStringUtils.randomAlphabetic(5) + UsersFactory.USER_EMAIL);
        return user;
    }

    public static ReadableUser createReadableUser() {
        return new UsersFactory.MockReadableUser(
                UsersFactory.USER_ID,
                UsersFactory.USER_EMAIL,
                UsersFactory.USER_FIRSTNAME,
                UsersFactory.USER_LASTNAME,
                true
        );

    }

    public static NewUserReq createNewUserReq() {
        final NewUserReq req = new NewUserReq(
                UsersFactory.USER_EMAIL,
                UsersFactory.USER_PASSWORD,
                UsersFactory.USER_FIRSTNAME,
                UsersFactory.USER_LASTNAME
        );
        return req;
    }

    private static User createUser(
            final Long id,
            final String email,
            final String firstName,
            final String lastName,
            final boolean isActivated
    ) {

        final User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setPassword(RandomStringUtils.random(60));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setActivated(isActivated);
        return user;
    }

    public static UpdateUserReq createUpdateUserReq() {
        return new UpdateUserReq(UsersFactory.UPDATED_USER_FIRSTNAME, UsersFactory.UPDATED_USER_LASTNAME);
    }

    public static class MockReadableUser implements ReadableUser {

        private final Long id;
        private final String email;
        private final String firstName;
        private final String lastName;
        private final boolean activated;

        MockReadableUser(
                final Long id,
                final String email,
                final String firstName,
                final String lastName,
                final boolean activated
        ) {

            this.id = id;
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
            this.activated = activated;
        }

        @Override
        public Long getId() {
            return this.id;
        }

        @Override
        public String getEmail() {
            return this.email;
        }

        @Override
        public String getFirstName() {
            return this.firstName;
        }

        @Override
        public String getLastName() {
            return this.lastName;
        }

        @Override
        public Boolean getActivated() {
            return this.activated;
        }
    }
}
