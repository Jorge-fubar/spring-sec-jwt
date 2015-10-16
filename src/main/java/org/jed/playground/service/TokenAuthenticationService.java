package org.jed.playground.service;

import org.jed.playground.auth.TokenHandler;
import org.jed.playground.auth.UserAuthentication;
import org.jed.playground.service.impl.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.crypto.KeyGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.SignatureException;

/**
 * @author Jorge España
 */
@Slf4j
public class TokenAuthenticationService {

    private UserDetailsService userDetailsService;

    public TokenAuthenticationService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    private static final TokenHandler tokenHandler = new TokenHandler(generateSecretKey());

    public String addAuthentication(Authentication user) {
        return tokenHandler.createTokenForUser(user.getName());
    }

    public Authentication getAuthentication(String token) throws SignatureException {
        if (StringUtils.isNotEmpty(token)) {
            final String username = tokenHandler.parseUserFromToken(token);
            if (username != null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                return new UserAuthentication(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
            }
        }
        return null;
    }

    public boolean isTokenValid(final String token, final String tenant) {
        if (StringUtils.isEmpty(token) || StringUtils.isEmpty(tenant)) {
            return false;
        }
        final String username;
        try {
            username = tokenHandler.parseUserFromToken(token);
        } catch (SignatureException e) {
            log.warn("Token parsing threw an exception.", e.getMessage());
            return false;
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String userDetailsTenant = ((UserDetailsImpl) userDetails).getLoginUser().getTenant();
        if (StringUtils.equals(tenant,userDetailsTenant)) {
            return true;
        }
        log.warn(String.format("User \"%s\" trying to access to tenant \"%s\" instead of its own tenant \"%s\". " +
                "Request unauthorized", userDetails.getUsername(), tenant, userDetailsTenant));
        return false;
    }

    private static final String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        random.setSeed(1000L);
        try {
            KeyGenerator keygen = KeyGenerator.getInstance("HmacSHA256");
            keygen.init(256, random);
            javax.crypto.SecretKey key = keygen.generateKey();
            return key.getEncoded().toString();
        } catch (NoSuchAlgorithmException e) {
            if (log.isDebugEnabled()) {
                log.error("IMPORTANT!! Application could not generate a random secret key, reason: ", e);
                log.error("Falling back to default secret key");
            }
            return "defaultGeneratedSecretKey";
        }
    }

}
