package com.devcomanda.demospringsecurity.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
@Configuration
@EnableJpaRepositories("com.devcomanda.demospringsecurity.repositories")
@EnableTransactionManagement
public class DatabaseConfiguration {
}
