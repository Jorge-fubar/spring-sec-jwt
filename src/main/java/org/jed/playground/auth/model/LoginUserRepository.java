package org.jed.playground.auth.model;

import org.springframework.data.repository.CrudRepository;

/**
 * @author Jorge España
 */
public interface LoginUserRepository extends CrudRepository<LoginUser, Long> {

    LoginUser findByUsername(String username);
}
