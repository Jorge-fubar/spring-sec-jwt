package org.jed.playground.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.security.SignatureException;

/**
 * @author Jorge España
 */
public final class TokenHandler {

    private final String secret;

    public TokenHandler(String secret) {
        this.secret = secret;
    }

    public String parseUserFromToken(String token) throws SignatureException {
        try {
            return Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        }
        catch (Exception e) {
            throw new SignatureException(e);
        }

    }

    public String createTokenForUser(String user) {
        return Jwts.builder()
                .setSubject(user)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }
}