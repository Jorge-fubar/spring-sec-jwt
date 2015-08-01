package org.jed.playground.config;

import org.jed.playground.auth.config.SecurityConfig;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.support.AbstractDispatcherServletInitializer;

import javax.servlet.*;
import java.util.EnumSet;

/**
 * @author Jorge España
 */
public class WebAppInitializer implements org.springframework.web.WebApplicationInitializer {
    private static final String SECURITY_FILTER_NAME = AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME;
    private static final String DISPATCHER_SERVLET_NAME = AbstractDispatcherServletInitializer.DEFAULT_SERVLET_NAME;

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        WebApplicationContext rootContext = createRootContext(servletContext);

        configureSpringMvc(servletContext, rootContext);
//        configureSpringSecurity(servletContext, rootContext);
    }

    private WebApplicationContext createRootContext(ServletContext servletContext) {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(WebMvcConfiguration.class, SecurityConfig.class);

        servletContext.addListener(new ContextLoaderListener(context));

        return context;
    }

    private void configureSpringMvc(ServletContext servletContext, WebApplicationContext rootContext) {
        DispatcherServlet dispatcher = new DispatcherServlet(rootContext);

        ServletRegistration.Dynamic servlet = servletContext.addServlet(DISPATCHER_SERVLET_NAME, dispatcher);
        servlet.setLoadOnStartup(1);
        servlet.addMapping("/*");
    }

    private void configureSpringSecurity(ServletContext servletContext, WebApplicationContext rootContext) {
        servletContext.setSessionTrackingModes(EnumSet.of(SessionTrackingMode.COOKIE));

        DelegatingFilterProxy proxy = new DelegatingFilterProxy(SECURITY_FILTER_NAME, rootContext);

        FilterRegistration.Dynamic filter = servletContext.addFilter(SECURITY_FILTER_NAME, proxy);
        filter.setAsyncSupported(true);
        filter.addMappingForServletNames(EnumSet.allOf(DispatcherType.class), false, DISPATCHER_SERVLET_NAME);
    }
}
