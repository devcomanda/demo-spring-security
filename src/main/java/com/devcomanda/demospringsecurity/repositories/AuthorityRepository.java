package com.devcomanda.demospringsecurity.repositories;

import com.devcomanda.demospringsecurity.model.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
@Repository
public interface AuthorityRepository extends JpaRepository<Authority, String> {
}
