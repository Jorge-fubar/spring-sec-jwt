package org.jed.playground.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Jorge España
 */
public interface GreetingRepository extends CrudRepository<Greeting, Long> {

    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    <S extends Greeting> S save(S entity);
}
