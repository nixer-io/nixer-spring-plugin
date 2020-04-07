package io.nixer.example.integrationTest.detection;

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
import static org.hamcrest.Matchers.nullValue;
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
        final Integer threshold = ruleProperties.getFailedLoginThreshold().get(useragent).getThreshold();
        final String givenUserAgent = "given-user-agent";

        // Saturate the failed login counter for the given user agent.
        for (int i = 0; i <= threshold; i++) {
            givenFailedLoginAttempt(dummyUsername(i), givenUserAgent, dummyIp(i))
                    .andExpect(request().attribute(USER_AGENT_FAILED_LOGIN_OVER_THRESHOLD, false))
                    .andExpect(request().attribute(IP_FAILED_LOGIN_OVER_THRESHOLD, false))
                    .andExpect(request().attribute(USERNAME_FAILED_LOGIN_OVER_THRESHOLD, nullValue()));
        }

        givenFailedLoginAttempt(
                dummyUsername(threshold + 1), givenUserAgent, dummyIp(threshold + 1)
        )
                .andExpect(request().attribute(USER_AGENT_FAILED_LOGIN_OVER_THRESHOLD, true))
                .andExpect(request().attribute(IP_FAILED_LOGIN_OVER_THRESHOLD, false))
                .andExpect(request().attribute(USERNAME_FAILED_LOGIN_OVER_THRESHOLD, nullValue()));
    }

    @Test
    void should_mark_number_of_failed_logins_for_single_username_is_over_threshold() throws Exception {
        final Integer threshold = ruleProperties.getFailedLoginThreshold().get(username).getThreshold();
        final String givenUsername = "given-username";

        // Saturate the failed login counter for the given username.
        for (int i = 0; i <= threshold; i++) {
            givenFailedLoginAttempt(givenUsername, dummyUserAgent(i), dummyIp(i + 100))
                    .andExpect(request().attribute(USER_AGENT_FAILED_LOGIN_OVER_THRESHOLD, false))
                    .andExpect(request().attribute(IP_FAILED_LOGIN_OVER_THRESHOLD, false))
                    .andExpect(request().attribute(USERNAME_FAILED_LOGIN_OVER_THRESHOLD, nullValue()));
        }

        givenFailedLoginAttempt(
                givenUsername, dummyUserAgent(threshold + 1), dummyIp(threshold + 1 + 100)
        )
                .andExpect(request().attribute(USER_AGENT_FAILED_LOGIN_OVER_THRESHOLD, false))
                .andExpect(request().attribute(IP_FAILED_LOGIN_OVER_THRESHOLD, false))
                .andExpect(request().attribute(USERNAME_FAILED_LOGIN_OVER_THRESHOLD, true));
    }

    @Test
    void should_mark_number_of_failed_logins_for_single_ip_address_is_over_threshold() throws Exception {
        final Integer threshold = ruleProperties.getFailedLoginThreshold().get(ip).getThreshold();
        final String givenIpAddress = "10.9.8.7";

        // Saturate the failed login counter for the given IP address.
        for (int i = 0; i <= threshold; i++) {
            givenFailedLoginAttempt(dummyUsername(i) + "_ip", dummyUserAgent(i) + "_ip", givenIpAddress)
                    .andExpect(request().attribute(USER_AGENT_FAILED_LOGIN_OVER_THRESHOLD, false))
                    .andExpect(request().attribute(IP_FAILED_LOGIN_OVER_THRESHOLD, false))
                    .andExpect(request().attribute(USERNAME_FAILED_LOGIN_OVER_THRESHOLD, nullValue()));
        }

        givenFailedLoginAttempt(
                dummyUsername(threshold + 1) + "_ip", dummyUserAgent(threshold + 1) + "_ip", givenIpAddress
        )
                .andExpect(request().attribute(USER_AGENT_FAILED_LOGIN_OVER_THRESHOLD, false))
                .andExpect(request().attribute(IP_FAILED_LOGIN_OVER_THRESHOLD, true))
                .andExpect(request().attribute(USERNAME_FAILED_LOGIN_OVER_THRESHOLD, nullValue()));
    }

    private ResultActions givenFailedLoginAttempt(final String username, final String userAgent, final String ip) throws Exception {
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

    private static String dummyIp(int i) {
        return Joiner.on('.').join(
                (i + 1) % 256,
                (i + 2) % 256,
                (i + 3) % 256,
                (i + 4) % 256
        );
    }

    private static String dummyUsername(final int i) {
        return "dummy-username_" + i;
    }

    private static String dummyUserAgent(final int i) {
        return "dummy-user-agent_" + i;
    }
}
