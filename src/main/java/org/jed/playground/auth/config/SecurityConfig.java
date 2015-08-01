package org.jed.playground.auth.config;

import org.jed.playground.auth.HttpAuthenticationEntryPoint;
import org.jed.playground.auth.StatelessAuthenticationFilter;
import org.jed.playground.auth.handler.AuthFailureHandler;
import org.jed.playground.auth.handler.HttpLogoutSuccessHandler;
import org.jed.playground.service.TokenAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * @author Jorge España
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private static final String LOGIN_ANT_PATH = "/[\\w/]*login";
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private HttpAuthenticationEntryPoint authenticationEntryPoint;
    @Autowired
    private AuthFailureHandler authFailureHandler;
    @Autowired
    private HttpLogoutSuccessHandler logoutSuccessHandler;

    @Bean
    public TokenAuthenticationService tokenAuthenticationService() {
        return new TokenAuthenticationService(userDetailsService);
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(new ShaPasswordEncoder());

        return authenticationProvider;
    }

//    @Bean
//    public JsonAuthenticationFilter jsonAuthenticationFilter() throws Exception {
//        JsonAuthenticationFilter authFilter = new JsonAuthenticationFilter(authenticationManager());
//        authFilter.setFilterProcessesUrl("/login");
//        authFilter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/login", "POST"));
//        authFilter.setAuthenticationManager(authenticationManager());
//        authFilter.setAuthenticationFailureHandler(authFailureHandler);
//        authFilter.setUsernameParameter("username");
//        authFilter.setPasswordParameter("password");
//        return authFilter;
//    }

    public StatelessAuthenticationFilter statelessAuthenticationFilter() throws Exception {
        StatelessAuthenticationFilter statelessAuthFilter = new StatelessAuthenticationFilter(tokenAuthenticationService());
        statelessAuthFilter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(LOGIN_ANT_PATH, "POST"));
        statelessAuthFilter.setAuthenticationManager(authenticationManager());
        statelessAuthFilter.setAuthenticationFailureHandler(authFailureHandler);
        statelessAuthFilter.setUsernameParameter("username");
        statelessAuthFilter.setPasswordParameter("password");
        return statelessAuthFilter;
    }

    @Bean
    @Override
    public UserDetailsService userDetailsServiceBean() throws Exception {
        return super.userDetailsServiceBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http.csrf().disable()
                .authenticationProvider(authenticationProvider())
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .and()
                .logout()
                .permitAll()
                .logoutRequestMatcher(new AntPathRequestMatcher("/login", "DELETE"))
                .logoutSuccessHandler(logoutSuccessHandler)
                .and()
                .sessionManagement()
                .maximumSessions(1);
        http
                .authorizeRequests()
                .antMatchers("**/^login").authenticated()
                .antMatchers("**/login").permitAll()
                .and()
                .addFilterBefore(statelessAuthenticationFilter(),
                        UsernamePasswordAuthenticationFilter.class);
        // @formatter:on
    }
}