package org.jed.playground.service.impl;

import org.jed.playground.auth.model.LoginUser;
import org.jed.playground.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Jorge España
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserService userService;

    @Autowired
    UserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LoginUser loginUser = userService.getUserByUsername(username);
        if (loginUser == null) {
            throw new UsernameNotFoundException(username);
        }
        loginUser.setPassword(new ShaPasswordEncoder().encodePassword(loginUser.getPassword(), null));
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.addAll(loginUser.getAuthorities().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
        return new UserDetailsImpl(loginUser, grantedAuthorities);
    }
}