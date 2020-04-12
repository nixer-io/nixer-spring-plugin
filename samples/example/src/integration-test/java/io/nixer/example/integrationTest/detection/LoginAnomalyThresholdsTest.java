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
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static io.nixer.example.integrationTest.LoginRequestBuilder.formLogin;
import static io.nixer.nixerplugin.core.detection.config.AnomalyRulesProperties.Name.useragent;
import static io.nixer.nixerplugin.core.detection.filter.RequestMetadata.USER_AGENT_FAILED_LOGIN_OVER_THRESHOLD;
import static org.springframework.http.HttpHeaders.USER_AGENT;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@EnableAutoConfiguration(exclude = InfluxMetricsExportAutoConfiguration.class)
class LoginAnomalyThresholdsTest {

    private static final String FAKE_USER_AGENT = "user-agent";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AnomalyRulesProperties ruleProperties;

    @Test
    void shouldSetFlagThatUserAgentOverThreshold() throws Exception {
        final Integer failedLoginsPerUserAgentThreshold = ruleProperties.getFailedLoginThreshold().get(useragent).getThreshold();

        for (int i = 0; i < failedLoginsPerUserAgentThreshold + 1; i++) {
            this.mockMvc.perform(formLogin().user("user").password("guess").build()
                    .header(USER_AGENT, FAKE_USER_AGENT)
                    .with(remoteAddress(sampleIp(i))))
                    .andExpect(unauthenticated())
                    .andExpect(request().attribute(USER_AGENT_FAILED_LOGIN_OVER_THRESHOLD, false));
        }

        this.mockMvc.perform(formLogin().user("user").password("guess").build()
                .header(USER_AGENT, FAKE_USER_AGENT))
                .andExpect(unauthenticated())
                .andExpect(request().attribute(USER_AGENT_FAILED_LOGIN_OVER_THRESHOLD, true));
    }

    private static RequestPostProcessor remoteAddress(String ip) {
        return request -> {
            request.setRemoteAddr(ip);
            return request;
        };
    }

    private static String sampleIp(int i) {
        return Joiner.on('.').join(
                (i + 1) % 256,
                (i + 2) % 256,
                (i + 3) % 256,
                (i + 4) % 256
        );
    }
}
