package org.jed.playground.service;

import org.jed.playground.auth.model.LoginUser;

import java.util.List;

/**
 * @author Jorge España
 */
public interface UserService {
    LoginUser getUserByUsername(String username);

    List<String> getPermissions(String username);

    Boolean isCurrentUserLoggedIn();
}
