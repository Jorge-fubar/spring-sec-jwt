package org.jed.playground.mvc;

import org.jed.playground.auth.model.LoginUser;
import org.jed.playground.data.Greeting;
import org.jed.playground.service.TokenAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.Collections;

/**
 * @author Jorge España
 */
@RestController
@RequestMapping(LoginController.PATH)
public class LoginController {

    public static final String PATH = "/{tenant}/login";
    @Autowired
    AuthenticationProvider authenticationProvider;
    @Autowired
    MappingJackson2HttpMessageConverter jacksonConverter;
    @Autowired
    private TokenAuthenticationService tokenAuthenticationService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public Greeting find() {
        Greeting greeting = new Greeting();
        greeting.setSummary("summary");
        greeting.setCreated(Calendar.getInstance());
        greeting.setText("text of the greeting");
        return greeting;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> login(@RequestBody LoginUser loginUser) {
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(loginUser.getUsername(), loginUser.getPassword());
        User details = new User(
                loginUser.getUsername(),
                loginUser.getPassword(),
                Collections.<GrantedAuthority>emptyList());
        token.setDetails(details);
        try {
            Authentication auth = authenticationProvider.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
            final String jwtToken = tokenAuthenticationService.addAuthentication(auth);
            return new ResponseEntity<>(jwtToken, HttpStatus.ACCEPTED);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }
}
