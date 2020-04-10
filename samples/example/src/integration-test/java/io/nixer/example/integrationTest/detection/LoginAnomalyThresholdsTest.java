package io.nixer.example.integrationTest.detection;

import java.util.Random;

import com.google.common.base.Joiner;
import io.nixer.nixerplugin.core.detection.config.AnomalyRulesProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.metrics.export.influx.InfluxMetricsExportAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static io.nixer.example.integrationTest.LoginRequestBuilder.formLogin;
import static io.nixer.nixerplugin.core.detection.config.AnomalyRulesProperties.Name.ip;
import static io.nixer.nixerplugin.core.detection.config.AnomalyRulesProperties.Name.useragent;
import static io.nixer.nixerplugin.core.detection.config.AnomalyRulesProperties.Name.username;
import static io.nixer.nixerplugin.core.detection.filter.RequestMetadata.IP_FAILED_LOGIN_OVER_THRESHOLD;
import static io.nixer.nixerplugin.core.detection.filter.RequestMetadata.USERNAME_FAILED_LOGIN_OVER_THRESHOLD;
import static io.nixer.nixerplugin.core.detection.filter.RequestMetadata.USER_AGENT_FAILED_LOGIN_OVER_THRESHOLD;
import static org.springframework.http.HttpHeaders.USER_AGENT;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@EnableAutoConfiguration(exclude = InfluxMetricsExportAutoConfiguration.class)
class LoginAnomalyThresholdsTest {

    private static final String INVALID_PASSWORD = "guess";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AnomalyRulesProperties ruleProperties;

    @Test
    void should_mark_number_of_failed_logins_for_single_user_agent_is_over_threshold() throws Exception {
        final Random random = new Random(1);
        final Integer threshold = ruleProperties.getFailedLoginThreshold().get(useragent).getThreshold();
        final String givenUserAgent = "given-user-agent";

        // Saturate the failed login counter for the given user agent.
        for (int i = 0; i <= threshold; i++) {
            expectNoThresholdsExceeded(
                    doFailedLoginAttempt(dummyUsername(random), givenUserAgent, dummyIp(random))
            );
        }

        doFailedLoginAttempt(dummyUsername(random), givenUserAgent, dummyIp(random))
                .andExpect(request().attribute(USER_AGENT_FAILED_LOGIN_OVER_THRESHOLD, true))
                .andExpect(request().attribute(IP_FAILED_LOGIN_OVER_THRESHOLD, false))
                .andExpect(request().attribute(USERNAME_FAILED_LOGIN_OVER_THRESHOLD, false));
    }

    @Test
    void should_mark_number_of_failed_logins_for_single_username_is_over_threshold() throws Exception {
        final Random random = new Random(2);
        final Integer threshold = ruleProperties.getFailedLoginThreshold().get(username).getThreshold();
        final String givenUsername = "given-username";

        // Saturate the failed login counter for the given username.
        for (int i = 0; i <= threshold; i++) {
            expectNoThresholdsExceeded(
                    doFailedLoginAttempt(givenUsername, dummyUserAgent(random), dummyIp(random))
            );
        }

        doFailedLoginAttempt(givenUsername, dummyUserAgent(random), dummyIp(random))
                .andExpect(request().attribute(USER_AGENT_FAILED_LOGIN_OVER_THRESHOLD, false))
                .andExpect(request().attribute(IP_FAILED_LOGIN_OVER_THRESHOLD, false))
                .andExpect(request().attribute(USERNAME_FAILED_LOGIN_OVER_THRESHOLD, true));
    }

    @Test
    void should_mark_number_of_failed_logins_for_single_ip_address_is_over_threshold() throws Exception {
        final Random random = new Random(3);
        final Integer threshold = ruleProperties.getFailedLoginThreshold().get(ip).getThreshold();
        final String givenIpAddress = "10.9.8.7";

        // Saturate the failed login counter for the given IP address.
        for (int i = 0; i <= threshold; i++) {
            expectNoThresholdsExceeded(
                    doFailedLoginAttempt(dummyUsername(random), dummyUserAgent(random), givenIpAddress)
            );
        }

        doFailedLoginAttempt(dummyUsername(random), dummyUserAgent(random), givenIpAddress)
                .andExpect(request().attribute(USER_AGENT_FAILED_LOGIN_OVER_THRESHOLD, false))
                .andExpect(request().attribute(IP_FAILED_LOGIN_OVER_THRESHOLD, true))
                .andExpect(request().attribute(USERNAME_FAILED_LOGIN_OVER_THRESHOLD, false));
    }

    private static void expectNoThresholdsExceeded(final ResultActions resultActions) throws Exception {
        resultActions
                .andExpect(request().attribute(USER_AGENT_FAILED_LOGIN_OVER_THRESHOLD, false))
                .andExpect(request().attribute(IP_FAILED_LOGIN_OVER_THRESHOLD, false))
                .andExpect(request().attribute(USERNAME_FAILED_LOGIN_OVER_THRESHOLD, false));
    }

    private ResultActions doFailedLoginAttempt(final String username, final String userAgent, final String ip) throws Exception {
        return this.mockMvc
                .perform(
                        formLogin()
                                .user(username).password(INVALID_PASSWORD).build()
                                .header(USER_AGENT, userAgent)
                                .with(remoteAddress(ip))
                )
                .andExpect(unauthenticated());
    }

    private static RequestPostProcessor remoteAddress(String ip) {
        return request -> {
            request.setRemoteAddr(ip);
            return request;
        };
    }

    private static String dummyIp(final Random random) {
        return Joiner.on('.').join(
                random.nextInt(256),
                random.nextInt(256),
                random.nextInt(256),
                random.nextInt(256)
        );
    }

    private static String dummyUsername(final Random random) {
        return "username_" + random.nextLong();
    }

    private static String dummyUserAgent(final Random random) {
        return "user-agent_" + random.nextLong();
    }
}
