package com.devcomanda.demospringsecurity.repositories;

import com.devcomanda.demospringsecurity.model.User;
import com.devcomanda.demospringsecurity.model.projections.ReadableUser;
import com.devcomanda.demospringsecurity.model.projections.UpdatableUser;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findOneByActivationKey(String activationKey);

    List<User> findAllByActivatedIsFalseAndCreatedDateBefore(Instant dateTime);

    Optional<User> findOneByEmailIgnoreCase(String email);

    @EntityGraph(attributePaths = "authorities")
    Optional<User> findOneWithAuthoritiesByEmailIgnoreCase(String email);

    Optional<ReadableUser> findAsReadableByEmailIgnoreCase(String email);

    @Query("select distinct u.id as id, u.email as email, u.lastName as lastName, u.firstName as firstName, u.activated as activated " +
            "from User u join u.authorities at where at.name in (:authority) order by id")
    List<ReadableUser> findAsReadableByAuthority(@Param("authority") String authority);

    Optional<User> findOneById(Long userId);

    Optional<UpdatableUser> findAsUpdatableUserById(Long userId);

    Optional<ReadableUser> findOneReadableById(Long userId);
}
