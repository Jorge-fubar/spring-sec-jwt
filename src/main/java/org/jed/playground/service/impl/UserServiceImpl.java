package org.jed.playground.service.impl;

import org.jed.playground.auth.model.LoginUser;
import org.jed.playground.auth.model.LoginUserRepository;
import org.jed.playground.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Jorge España
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired private LoginUserRepository loginUserRepository;

    @Override
    @Transactional("authTransactionManager")
    public LoginUser getUserByUsername(String username) {
        return loginUserRepository.findByUsername(username);
    }

    @Override
    public List<String> getPermissions(String username) {
        LoginUser loginUser = loginUserRepository.findByUsername(username);
        return loginUser == null? null : loginUser.getAuthorities();
    }

    @Override
    public Boolean isCurrentUserLoggedIn() {
        return true;
    }
}