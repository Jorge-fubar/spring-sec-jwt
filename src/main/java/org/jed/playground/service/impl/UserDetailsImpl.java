package org.jed.playground.service.impl;

import org.jed.playground.auth.model.LoginUser;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * @author Jorge España
 */
public class UserDetailsImpl implements UserDetails {
    @Getter
    @Setter
    private LoginUser loginUser;
    private List<GrantedAuthority> authorities;

    public UserDetailsImpl(LoginUser loginUser, List<GrantedAuthority> authorities) {
        this.loginUser = loginUser;
        this.authorities = authorities;
    }

    @Override
    public String getPassword() {
        return loginUser.getPassword();
    }

    @Override
    public String getUsername() {
        return loginUser.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
}
