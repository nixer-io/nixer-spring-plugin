package eu.xword.nixer.nixerplugin.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.boot.web.servlet.filter.OrderedRequestContextFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.filter.RequestContextFilter;

/**
 * Very basic in-memory single user IAM.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsChecker captchaChecker;

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeRequests()
                .requestMatchers(EndpointRequest.to(MetricsEndpoint.class))
                .permitAll()
                .antMatchers("/*")
                .authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .and()
                .logout()
                    .logoutUrl("/logout")
                .permitAll()

        ;


    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        auth.inMemoryAuthentication()
                .passwordEncoder(encoder)
                .withUser("user").password(encoder.encode("user")).roles("USER")
                .and()
                //.apply() TODO consider creating custom Configurer for Recaptcha
                .addObjectPostProcessor(new ObjectPostProcessor<DaoAuthenticationProvider>() {
                    @Override
                    public DaoAuthenticationProvider postProcess(final DaoAuthenticationProvider object) {
                        object.setPreAuthenticationChecks(captchaChecker);
                        object.setHideUserNotFoundExceptions(false);
                        return object;
                    }
                });

    }


    //TODO should we expect it to be registered or should we register it if missing. What about other methods to register request context

//    RequestContextListener doesn't set response
//    @Bean
//    public RequestContextListener requestContextListener() {
//        return new RequestContextListener();
//    }

//    @Bean
//    public RequestContextFilter requestContextFilter() {
//        return new RequestContextFilter();
//    }


    @Bean
    public RequestContextFilter requestContextFilter() {
        OrderedRequestContextFilter filter = new OrderedRequestContextFilter();
        filter.setOrder(-100001);
        return filter;
    }
}
