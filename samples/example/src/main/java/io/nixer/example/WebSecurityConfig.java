package io.nixer.example;

import java.util.LinkedHashMap;
import javax.sql.DataSource;

import io.nixer.nixerplugin.captcha.config.CaptchaConfigurer;
import io.nixer.nixerplugin.captcha.security.CaptchaChecker;
import io.nixer.nixerplugin.core.detection.filter.FilterConfiguration;
import io.nixer.nixerplugin.core.detection.filter.behavior.Conditions;
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

import static io.nixer.nixerplugin.core.detection.filter.behavior.Behaviors.BLOCKED_ERROR;
import static io.nixer.nixerplugin.core.detection.filter.behavior.Behaviors.CAPTCHA;
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
                .withObjectPostProcessor(new CaptchaConfigurer(captchaChecker)); // configure captcha

//        auth.inMemoryAuthentication()
//                .passwordEncoder(encoder)
//                .withUser("user").password(encoder.encode("user")).roles("USER")
//                .withObjectPostProcessor(new CaptchaConfigurer(captchaChecker));
    }

    @Bean
    public RequestContextFilter requestContextFilter() {
        return new OrderedRequestContextFilter();
    }

    /**
     * Configures rules. Rules define what happens at what conditions.
     */
    @Bean
    public FilterConfiguration.BehaviorProviderConfigurer behaviorConfigurer() {
        // todo make it possible to create rule from properties
        return builder -> builder
                    .rule("blacklistedIp")  // we want to hide fact that request was blocked. So pretending regular login error.
                    .when(Conditions::isBlacklistedIp)
                    .then(BLOCKED_ERROR)
                .buildRule()
                    .rule("ipLoginOverThreshold")
                    .when(Conditions::isIpLoginOverThreshold)
                    .then(CAPTCHA)
                .buildRule()
                    .rule("userAgentLoginOverThreshold")
                    .when(Conditions::isUserAgentLoginOverThreshold)
                    .then(CAPTCHA)
                .buildRule()
                    .rule("credentialStuffingActive")
                    .when(Conditions::isGlobalCredentialStuffing)
                    .then(CAPTCHA)
                .buildRule();
    }
}
