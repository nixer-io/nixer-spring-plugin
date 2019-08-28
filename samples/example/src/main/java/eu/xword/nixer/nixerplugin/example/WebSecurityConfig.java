package eu.xword.nixer.nixerplugin.example;

import java.util.LinkedHashMap;
import javax.sql.DataSource;

import eu.xword.nixer.nixerplugin.captcha.config.CaptchaConfigurer;
import eu.xword.nixer.nixerplugin.captcha.security.CaptchaChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.filter.OrderedRequestContextFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.DelegatingAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.web.filter.RequestContextFilter;

import static org.springframework.http.HttpMethod.POST;

/**
 * Very basic in-memory single user IAM.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CaptchaChecker captchaChecker;

    @Autowired
    private DataSource dataSource;

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        final LinkedHashMap<Class<? extends AuthenticationException>, AuthenticationFailureHandler> loginFailureHandlers = new LinkedHashMap<>();
        loginFailureHandlers.put(LockedException.class, new SimpleUrlAuthenticationFailureHandler("/login?error=LOCKED")); // TODO pass info
        httpSecurity
                .anonymous().and()
                .authorizeRequests()
                .antMatchers("/login").permitAll()
                .antMatchers(POST, "/subscribeUser").anonymous()
                .antMatchers("/userSubscribe").permitAll()
                .antMatchers("/actuator/**").permitAll()
                .antMatchers("/assets/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin().loginPage("/login")
                .failureHandler(new DelegatingAuthenticationFailureHandler(loginFailureHandlers, new SimpleUrlAuthenticationFailureHandler("/login?error")))
//                    .failureHandler(new SimpleUrlAuthenticationFailureHandler("/login?error"))
                .and()
                .logout().logoutUrl("/logout").permitAll()
                .and().csrf()
                .ignoringAntMatchers("/actuator/**", "/subscribeUser")
        ;
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
//        auth.userDetailsService(null)
//                .addObjectPostProcessor(new CaptchaConfigurer(captchaChecker));
//
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .withDefaultSchema()
                .withUser("user").password(encoder.encode("user")).roles("USER").and()
                .withObjectPostProcessor(new CaptchaConfigurer(captchaChecker));

//        auth.inMemoryAuthentication()
//                .passwordEncoder(encoder)
//                .withUser("user").password(encoder.encode("user")).roles("USER")
//                .withObjectPostProcessor(new CaptchaConfigurer(captchaChecker));
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
