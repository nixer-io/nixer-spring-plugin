package io.nixer.example.detection;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.metrics.export.influx.InfluxMetricsExportAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static io.nixer.example.LoginRequestBuilder.formLogin;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@SpringBootTest
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude = InfluxMetricsExportAutoConfiguration.class)
public class IpBlacklistingTest {

    private static final String BLACKLISTED_IP_V6 = "5555:5555:5555:5555:5555:5555:5555:5555";
    private static final String BLACKLISTED_IP_V4 = "5.5.5.5";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldFailLoginFromBlacklistedIpv4() throws Exception {
        this.mockMvc.perform(
                formLogin().user("user").password("fake").build()
                        .with(remoteAddress(BLACKLISTED_IP_V4)))
                .andExpect(isBlocked());
    }

    @Test
    void shouldFailLoginFromBlacklistedIpv6() throws Exception {
        this.mockMvc.perform(
                formLogin().user("user").password("fake").build()
                        .with(remoteAddress(BLACKLISTED_IP_V6)))
                .andExpect(isBlocked());
    }

    private RequestPostProcessor remoteAddress(String ip) { // TODO duplicate
        return request -> {
            request.setRemoteAddr(ip);
            return request;
        };
    }

    private ResultMatcher isBlocked() {
        return redirectedUrl("/login?blockedError");
    }
}
