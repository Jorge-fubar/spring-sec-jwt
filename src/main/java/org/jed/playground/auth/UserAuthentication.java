package org.jed.playground.auth;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * @author Jorge España
 */
public class UserAuthentication implements Authentication {

    private final User user;
    private boolean authenticated = true;
    @Getter
    @Setter
    private String tenant;

    public UserAuthentication(User user) {
        this.user = user;
    }

    public UserAuthentication(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this.user = new User(username, password, authorities);
    }

    @Override
    public String getName() {
        return user.getUsername();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getAuthorities();
    }

    @Override
    public Object getCredentials() {
        return user.getPassword();
    }

    @Override
    public User getDetails() {
        return user;
    }

    @Override
    public Object getPrincipal() {
        return user.getUsername();
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }
}