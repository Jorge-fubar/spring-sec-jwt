package org.jed.playground.auth;

import org.jed.playground.auth.model.LoginUser;
import org.jed.playground.service.TokenAuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.SignatureException;

/**
 * This filter extracts the JWT token from the request and uses it
 * to authenticate the request in the Spring Security Context.
 * After the request has been processed, it deletes the authentication
 * from Spring Security Context, so the request isn't kept in session.
 *
 * @author Jorge España
 */
@Slf4j
public class StatelessAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final String AUTH_HEADER_NAME = "Authorization";
    private final TokenAuthenticationService authenticationService;

    public StatelessAuthenticationFilter(TokenAuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        String username = obtainUsername(request);
        String password = obtainPassword(request);
        LoginUser loginUser = new LoginUser();
        writeJson(loginUser, response);

        username = username.trim();
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);

        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = ((HttpServletResponse) response);
        final String token = httpRequest.getHeader(AUTH_HEADER_NAME);
        if (httpRequest.getPathInfo().matches("/[\\w/]*login")) {
            filterChain.doFilter(request, response);
        }
        else {
            final String tenant = StringUtils.split(httpRequest.getPathInfo(), "/")[0];
            if (authenticationService.isTokenValid(token, tenant)) {
                try {
                    Authentication authentication = authenticationService.getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    filterChain.doFilter(request, response);
                    SecurityContextHolder.getContext().setAuthentication(null);
                } catch (SignatureException e) {
                    httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                }
            } else {
                httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            }
        }
    }


    public void writeJson (Object jsonBean, HttpServletResponse response) {
        MediaType jsonMimeType = MediaType.APPLICATION_JSON;
        MappingJackson2HttpMessageConverter jacksonConverter= new MappingJackson2HttpMessageConverter();
        if (jacksonConverter.canWrite(jsonBean.getClass(), jsonMimeType)) {
            try {
                jacksonConverter.write(jsonBean, jsonMimeType, new ServletServerHttpResponse(response));
            } catch (Exception e) {
                log.error("Could not transform json to object");
            }
        } else {
            log.info("json Converter cant write class " +jsonBean.getClass() );
        }
    }
}
