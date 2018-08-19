package com.devcomanda.demospringsecurity.repositories;

import com.devcomanda.demospringsecurity.model.Authority;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
@RunWith(SpringRunner.class)
@DataJpaTest
public class AuthorityRepositoryIntTest {

    @Autowired
    private AuthorityRepository authorityRepository;

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:database/empty.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:database/authorities/oneAuthority.sql")
    public void shouldReturnAuthorityByName() {

        final String roleName = "ROLE_SYSTEM";

        final Optional<Authority> authority = this.authorityRepository.findById(roleName);

        assertThat(authority)
            .isPresent()
            .containsInstanceOf(Authority.class);

        final Authority actualAuthority = authority.get();
        assertThat(actualAuthority.getName()).isEqualToIgnoringCase(roleName);
    }
}
