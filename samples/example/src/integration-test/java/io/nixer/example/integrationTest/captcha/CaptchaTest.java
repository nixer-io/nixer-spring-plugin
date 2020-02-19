package io.nixer.example.integrationTest.captcha;

import io.nixer.nixerplugin.captcha.recaptcha.RecaptchaClientStub;
import io.nixer.nixerplugin.captcha.security.CaptchaChecker;
import io.nixer.nixerplugin.captcha.security.CaptchaCondition;
import io.nixer.nixerplugin.core.detection.config.AnomalyRulesProperties;
import io.nixer.nixerplugin.core.detection.events.IpFailedLoginOverThresholdEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.metrics.export.influx.InfluxMetricsExportAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import static io.nixer.example.integrationTest.LoginRequestBuilder.formLogin;
import static io.nixer.nixerplugin.core.detection.config.AnomalyRulesProperties.Name.ip;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(CaptchaTest.TestConfig.class)
@AutoConfigureTestDatabase
@EnableAutoConfiguration(exclude = InfluxMetricsExportAutoConfiguration.class)
@Transactional
public class CaptchaTest {

    private static final String LOGIN_PAGE = "/login";
    private static final String CAPTCHA_PARAM = "g-recaptcha-response";
    private static final String GOOD_CAPTCHA = "good-captcha";
    private static final String BAD_CAPTCHA = "bad-captcha";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CaptchaChecker captchaChecker;

    @Autowired
    private RecaptchaClientStub recaptchaClientStub;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private AnomalyRulesProperties ruleProperties;

    @TestConfiguration
    public static class TestConfig {
        @Bean
        @Primary
        public RecaptchaClientStub recaptchaClientStub() {
            return new RecaptchaClientStub();
        }
    }

    @BeforeEach
    void setup() {
        this.captchaChecker.setCaptchaCondition(CaptchaCondition.NEVER);

        recaptchaClientStub.recordValidCaptcha(GOOD_CAPTCHA);
        recaptchaClientStub.recordInvalidCaptcha(BAD_CAPTCHA);
    }

    @AfterEach
    void tearDown() throws Exception {
        mockMvc.perform(logout());
    }

    @Test
    void shouldLoginSuccessfullyWithGoodCaptcha() throws Exception {
        //enable captcha
        this.captchaChecker.setCaptchaCondition(CaptchaCondition.ALWAYS);

        this.mockMvc.perform(get(LOGIN_PAGE))
                .andExpect(status().isOk())
                .andExpect(captchaChallenge());

        this.mockMvc.perform(formLogin().user("user").password("user").captcha(GOOD_CAPTCHA).build())
                .andExpect(authenticated());
    }

    @Test
    void shouldFailLoginWithBadCaptcha() throws Exception {
        //enable captcha
        this.captchaChecker.setCaptchaCondition(CaptchaCondition.ALWAYS);

        this.mockMvc.perform(get(LOGIN_PAGE))
                .andExpect(status().isOk())
                .andExpect(captchaChallenge());

        this.mockMvc.perform(formLogin().user("user").password("guess").captcha(BAD_CAPTCHA).build())
                .andExpect(unauthenticated());
    }

    @Test
    void protectEndpointWithCaptcha() throws Exception {
        this.mockMvc.perform(post("/subscribeUser")
                .param(CAPTCHA_PARAM, GOOD_CAPTCHA))
                .andExpect(status().isOk());
    }

    @Test
    void protectEndpointWithCaptchaFailed() throws Exception {
        this.mockMvc.perform(post("/subscribeUser")
                .param(CAPTCHA_PARAM, BAD_CAPTCHA))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void shouldReturnCaptchaChallengeIfActivated() throws Exception {
        //enable captcha
        this.captchaChecker.setCaptchaCondition(CaptchaCondition.ALWAYS);

        // @formatter:off
        this.mockMvc.perform(get(LOGIN_PAGE))
                .andExpect(status().isOk())
                .andExpect(captchaChallenge());
        // @formatter:on
    }

    @Test
    void shouldActiveCaptchaChallengeOnIpDueToConsecutiveLoginFailures() throws Exception {
        // enable session controlled mode
        this.captchaChecker.setCaptchaCondition(CaptchaCondition.SESSION_CONTROLLED);

        final String attackerDeviceIp = "6.6.6.6";
        final MockHttpSession session = new MockHttpSession();
        // @formatter:on
        for (int i = 0; i < ruleProperties.getFailedLoginThreshold().get(ip).getThreshold() + 1; i++) {
            this.mockMvc.perform(formLogin().user("user").password("guess").build()
                    .session(session)
                    .with(remoteAddress(attackerDeviceIp)))
                    .andExpect(unauthenticated());
        }
        // @formatter:off
        this.mockMvc.perform(get(LOGIN_PAGE).session(session)
                .with(remoteAddress(attackerDeviceIp)))
                .andExpect(status().isOk())
                .andExpect(captchaChallenge());

        this.mockMvc.perform(formLogin().user("user").password("user").captcha(GOOD_CAPTCHA).build()
                .session(session)
                .with(remoteAddress(attackerDeviceIp)))
                .andExpect(authenticated());

        final String newDeviceIp = "192.168.1.1";
        this.mockMvc.perform(get(LOGIN_PAGE)
                .session(new MockHttpSession())
                .with(remoteAddress(newDeviceIp)))
                .andExpect(status().isOk())
                .andExpect(noCaptchaChallenge());
    }

    @Test
    void shouldActiveCaptchaOnNewSessions() throws Exception {
        // enable session controlled mode
        this.captchaChecker.setCaptchaCondition(CaptchaCondition.SESSION_CONTROLLED);

        final String attackerDeviceIp = "6.6.6.6";
        eventPublisher.publishEvent(new IpFailedLoginOverThresholdEvent(attackerDeviceIp));

        this.mockMvc.perform(get(LOGIN_PAGE)
                .with(remoteAddress(attackerDeviceIp)))
                .andExpect(status().isOk())
                .andExpect(captchaChallenge());
    }

    @Test
    void captchaEndpointToReturnCurrentCondition() throws Exception {
        this.mockMvc.perform(get("/actuator/captcha"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.condition", is("NEVER")));
    }

    @Test
    void captchaEndpointToUpdateCondition() throws Exception {
        final String newCondition = "{ \"condition\": \"ALWAYS\"}";
        this.mockMvc.perform(post("/actuator/captcha")
                .content(newCondition)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());

        this.mockMvc.perform(get("/actuator/captcha"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.condition", is("ALWAYS")));
    }

    private RequestPostProcessor remoteAddress(String ip) { // TODO duplicate
        return request -> {
            request.setRemoteAddr(ip);
            return request;
        };
    }

    private ResultMatcher captchaChallenge() {
        return content().string(containsString("class=\"g-recaptcha\""));
    }

    private ResultMatcher noCaptchaChallenge() {
        return content().string(not(containsString("class=\"g-recaptcha\"")));
    }
}
