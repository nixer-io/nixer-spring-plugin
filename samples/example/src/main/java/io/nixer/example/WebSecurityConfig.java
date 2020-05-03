package io.nixer.example;

import java.util.LinkedHashMap;
import javax.sql.DataSource;

import io.nixer.nixerplugin.captcha.security.CaptchaAuthenticationProvider;
import io.nixer.nixerplugin.captcha.security.CaptchaChecker;
import io.nixer.nixerplugin.core.detection.filter.FilterConfiguration;
import io.nixer.nixerplugin.core.detection.filter.behavior.Conditions;
import io.nixer.nixerplugin.stigma.rules.StigmaConditions;
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

    @Autowired
    private CaptchaAuthenticationProvider captchaAuthenticationProvider;


    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        final LinkedHashMap<Class<? extends AuthenticationException>, AuthenticationFailureHandler> loginFailureHandlers = new LinkedHashMap<>();
        loginFailureHandlers.put(LockedException.class, new SimpleUrlAuthenticationFailureHandler("/login?error=LOCKED"));

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

        //JDBC Authentication with additional authentication provider.
        // Captcha checked before credentials - captcha information will be propagated in Nixer plugin.
        // Captcha will be checked always when displayed, and will take precedence before credentials check.
        auth
                .authenticationProvider(captchaAuthenticationProvider)
                .jdbcAuthentication()
                .dataSource(dataSource)
                .withDefaultSchema()
                .withUser("user").password(encoder.encode("user")).roles("USER");

//        //JDBC Authentication with post-processor mechanism.
//        //Credentials take precedence before captcha.
        //Invalid captcha will not be reported when captcha is empty and credentials are correct (though the authentication will fail as expected).
//        auth
//                .jdbcAuthentication()
//                .dataSource(dataSource)
//                .withDefaultSchema()
//                .withUser("user").password(encoder.encode("user")).roles("USER")
//                .and()
//                .withObjectPostProcessor(new CaptchaConfigurer(captchaChecker)); // configure captcha


//         //In-memory Authentication with post-processor mechanism.
//         auth.inMemoryAuthentication()
//                .passwordEncoder(encoder)
//                .withUser("user").password(encoder.encode("user")).roles("USER")
//                .withObjectPostProcessor(new CaptchaConfigurer(captchaChecker));

    }

    /**
     * Plugin needs access to HttpServletRequest, because of that we need this been to set RequestContextHolder and
     * make it possible for spring to inject proxy for request.
     */
    @Bean
    public RequestContextFilter requestContextFilter() {
        return new OrderedRequestContextFilter();
    }

    /**
     * Configures rules. Rules define what happens at what conditions.
     */
    @Bean
    public FilterConfiguration.BehaviorProviderConfigurer behaviorConfigurer() {
        // @formatter:off
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
                .buildRule()
                    .rule("failedLoginRatioActive")
                    .when(Conditions::isFailedLoginRatioActive)
                    .then(CAPTCHA)
                .buildRule()
                    .rule("revokedStigmaUsage")
                    .when(StigmaConditions::isStigmaRevoked)
                    .then(BLOCKED_ERROR)
                .buildRule()
                ;
        // @formatter:on
    }
}
