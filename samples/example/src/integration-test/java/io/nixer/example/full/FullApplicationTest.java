package io.nixer.example.full;

import java.util.Random;
import javax.servlet.http.Cookie;

import com.google.common.base.Joiner;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.nixer.nixerplugin.captcha.recaptcha.RecaptchaClientStub;
import io.nixer.nixerplugin.captcha.security.CaptchaChecker;
import io.nixer.nixerplugin.captcha.security.CaptchaCondition;
import io.nixer.nixerplugin.core.detection.config.AnomalyRulesProperties;
import io.nixer.nixerplugin.core.detection.events.IpFailedLoginOverThresholdEvent;
import io.nixer.nixerplugin.core.detection.filter.behavior.Behaviors;
import io.nixer.nixerplugin.core.login.metrics.LoginCounters;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.export.influx.InfluxMetricsExportAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.integration.test.matcher.MapContentMatchers;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.SmartRequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static io.nixer.example.LoginRequestBuilder.formLogin;
import static io.nixer.nixerplugin.core.detection.config.AnomalyRulesProperties.Name.ip;
import static io.nixer.nixerplugin.core.detection.config.AnomalyRulesProperties.Name.useragent;
import static io.nixer.nixerplugin.core.detection.filter.RequestMetadata.USER_AGENT_FAILED_LOGIN_OVER_THRESHOLD;
import static io.nixer.nixerplugin.pwned.metrics.PwnedCheckCounters.METRIC_NAME;
import static io.nixer.nixerplugin.pwned.metrics.PwnedCheckCounters.NOT_PWNED_RESULT;
import static io.nixer.nixerplugin.pwned.metrics.PwnedCheckCounters.PWNED_RESULT;
import static io.nixer.nixerplugin.pwned.metrics.PwnedCheckCounters.RESULT_TAG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.http.HttpHeaders.USER_AGENT;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Import(FullApplicationTest.TestConfig.class)
@EnableAutoConfiguration(exclude = InfluxMetricsExportAutoConfiguration.class)
public class FullApplicationTest {

    private static final String LOGIN_PAGE = "/login";
    private static final String CAPTCHA_PARAM = "g-recaptcha-response";
    private static final String FAKE_USER_AGENT = "user-agent";
    private static final String GOOD_CAPTCHA = "good-captcha";
    private static final String BAD_CAPTCHA = "bad-captcha";
    private static final String BLACKLISTED_IP_V6 = "5555:5555:5555:5555:5555:5555:5555:5555";
    private static final String BLACKLISTED_IP_V4 = "5.5.5.5";

    @Value("${nixer.stigma.cookie}")
    private String stigmaCookie;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CaptchaChecker captchaChecker;

    @Autowired
    private RecaptchaClientStub recaptchaClientStub;

    @Autowired
    private MeterRegistry meterRegistry;

    @Autowired
    private AnomalyRulesProperties ruleProperties;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

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
    void shouldLoginSuccessfully() throws Exception {
        // @formatter:off
        loginSuccessfully();
        // @formatter:on
    }

    @Test
    void shouldAssignStigmaAfterSuccessfulLogin() throws Exception {
        final String stigmaToken = loginSuccessfully()
                .andExpect(cookie().exists(stigmaCookie))
                .andReturn().getResponse().getCookie(stigmaCookie).getValue();

        // subsequent successful login with valid stigma does not require stigma refresh
        loginSuccessfullyWithStigma(stigmaToken)
                .andExpect(cookie().doesNotExist(stigmaCookie));
    }

    @Test
    void shouldRefreshStigmaAfterFailedLogin() throws Exception {
        final String firstStigmaToken = loginFailure()
                .andExpect(cookie().exists(stigmaCookie))
                .andReturn().getResponse().getCookie(stigmaCookie).getValue();

        final String secondStigmaToken = loginFailureWithStigma(firstStigmaToken)
                .andExpect(cookie().exists(stigmaCookie))
                .andReturn().getResponse().getCookie(stigmaCookie).getValue();

        assertThat(secondStigmaToken)
                .isNotBlank()
                .isNotEqualTo(firstStigmaToken);
    }

    @Test
    void shouldRefreshInvalidStigmaAfterSuccessfulLogin() throws Exception {
        final String invalidStigmaToken = "invalid-stigma-token";

        final String newStigmaToken = loginSuccessfullyWithStigma(invalidStigmaToken)
                .andExpect(cookie().exists(stigmaCookie))
                .andReturn().getResponse().getCookie(stigmaCookie).getValue();

        assertThat(newStigmaToken)
                .isNotBlank()
                .isNotEqualTo(invalidStigmaToken);
    }

    @Test
    void shouldRefreshInvalidStigmaAfterFailedLogin() throws Exception {
        final String invalidStigmaToken = "invalid-stigma-token";

        final String newStigmaToken = loginFailureWithStigma(invalidStigmaToken)
                .andExpect(cookie().exists(stigmaCookie))
                .andReturn().getResponse().getCookie(stigmaCookie).getValue();

        assertThat(newStigmaToken)
                .isNotBlank()
                .isNotEqualTo(invalidStigmaToken);
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
    void shouldSetFlagThatUserAgentOverThreshold() throws Exception {
        // enable session controlled mode
        this.captchaChecker.setCaptchaCondition(CaptchaCondition.SESSION_CONTROLLED);

        // @formatter:on
        for (int i = 0; i < ruleProperties.getFailedLoginThreshold().get(useragent).getThreshold() + 1; i++) {
            this.mockMvc.perform(formLogin().user("user").password("guess").build()
                    .header(USER_AGENT, FAKE_USER_AGENT)
                    .with(remoteAddress(randomIp())))
                    .andExpect(unauthenticated())
                    .andExpect(request().attribute(USER_AGENT_FAILED_LOGIN_OVER_THRESHOLD, false));
        }
        // @formatter:off

        this.mockMvc.perform(formLogin().user("user").password("guess").build()
                .header(USER_AGENT, FAKE_USER_AGENT))
                .andExpect(unauthenticated())
                .andExpect(request().attribute(USER_AGENT_FAILED_LOGIN_OVER_THRESHOLD, true));
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
    void shouldFailLoginWithBadCaptcha() throws  Exception {
        //enable captcha
        this.captchaChecker.setCaptchaCondition(CaptchaCondition.ALWAYS);

        this.mockMvc.perform(get(LOGIN_PAGE))
                .andExpect(status().isOk())
                .andExpect(captchaChallenge());

        this.mockMvc.perform(formLogin().user("user").password("guess").captcha(BAD_CAPTCHA).build())
                .andExpect(unauthenticated());
    }

    @Test
    void shouldDetectPwnedPassword() throws Exception {

        loginWithNotPwnedPassword()
                .andExpect(request().attribute("nixer.pwned.password", nullValue()));

        loginWithPwnedPassword()
                .andExpect(request().attribute("nixer.pwned.password", true));
    }

    @Test
    void shouldWritePwnedPasswordMetrics() throws  Exception {
        // given
        final Counter pwnedPasswordCounter = givenPwnedCounter();
        final Counter notPwnedPasswordCounter = givenNotPwnedCounter();

        double initialPwnedCount = pwnedPasswordCounter.count();
        double initialNotPwnedCount = notPwnedPasswordCounter.count();

        // when
        loginWithNotPwnedPassword();

        // then
        assertThat(pwnedPasswordCounter.count()).isEqualTo(initialPwnedCount);
        assertThat(notPwnedPasswordCounter.count()).isEqualTo(initialNotPwnedCount + 1);

        // when
        loginWithPwnedPassword();

        // then
        assertThat(pwnedPasswordCounter.count()).isEqualTo(initialPwnedCount + 1);
        assertThat(notPwnedPasswordCounter.count()).isEqualTo(initialNotPwnedCount + 1);
    }

    private Counter givenPwnedCounter() {
        return meterRegistry.get(METRIC_NAME)
                .tag(RESULT_TAG, PWNED_RESULT).counter();
    }

    private Counter givenNotPwnedCounter() {
        return meterRegistry.get(METRIC_NAME)
                .tag(RESULT_TAG, NOT_PWNED_RESULT).counter();
    }

    private ResultActions loginWithNotPwnedPassword() throws Exception {
        return this.mockMvc.perform(formLogin().user("user").password("not-pwned-password").build());
    }

    private ResultActions loginWithPwnedPassword() throws Exception {
        // using password from pwned-database
        return this.mockMvc.perform(formLogin().user("user").password("foobar1").build());
    }

    @Test
    void protectEndpointWithCaptcha() throws  Exception {
        this.mockMvc.perform(post("/subscribeUser")
                .param(CAPTCHA_PARAM, GOOD_CAPTCHA))
                .andExpect(status().isOk());
    }

    @Test
    void protectEndpointWithCaptchaFailed() throws  Exception {
        this.mockMvc.perform(post("/subscribeUser")
                .param(CAPTCHA_PARAM, BAD_CAPTCHA))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void loginUserAccessProtected() throws Exception {
        // @formatter:off
        final SmartRequestBuilder loginRequest = formLogin().user("user").password("user").build();
        MvcResult mvcResult = this.mockMvc.perform(loginRequest)
                .andExpect(authenticated()).andReturn();
        // @formatter:on

        MockHttpSession httpSession = (MockHttpSession) mvcResult.getRequest().getSession(false);

        // @formatter:off
        this.mockMvc.perform(get("/").session(httpSession))
                .andExpect(status().isOk());
        // @formatter:on
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

    @Test
    void shouldReportLoginSuccessMetric() throws Exception {
        final Counter successLoginCounter = successLoginCounter();
        double successLoginCount = successLoginCounter.count();

        loginSuccessfully();

        assertThat(successLoginCounter.count()).isEqualTo(successLoginCount + 1);
    }

    @Test
    void shouldReportLoginFailedMetric() throws Exception {
        final Counter failLoginCounter = badPasswordLoginFailCounter();
        double failedLoginCount = failLoginCounter.count();

        loginFailure();

        assertThat(failLoginCounter.count()).isEqualTo(failedLoginCount + 1);
    }

    private Counter successLoginCounter() {
        return LoginCounters.LOGIN_SUCCESS.register(meterRegistry);
    }

    private Counter badPasswordLoginFailCounter() {
        return LoginCounters.LOGIN_FAILED_BAD_PASSWORD.register(meterRegistry);
    }

    @Test
    void shouldFailLoginFromBlacklistedIpv4() throws Exception {
        // @formatter:off
        this.mockMvc.perform(
                formLogin().user("user").password("fake").build()
                        .with(remoteAddress(BLACKLISTED_IP_V4)))
                .andExpect(isBlocked());
        // @formatter:on
    }

    @Test
    void shouldFailLoginFromBlacklistedIpv6() throws Exception {
        // @formatter:off
        this.mockMvc.perform(
                formLogin().user("user").password("fake").build()
                        .with(remoteAddress(BLACKLISTED_IP_V6)))
                .andExpect(isBlocked());
        // @formatter:on
    }

    @Test
    void behaviorsEndpointToReturnBehaviorsAndRules() throws Exception {
        this.mockMvc.perform(get("/actuator/behaviors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.behaviors", hasItems(Behaviors.CAPTCHA.name(), Behaviors.LOG.name())))
                .andExpect(jsonPath("$.rules", MapContentMatchers.hasKey("credentialStuffingActive")))
        ;
    }

    @Test
    void behaviorsEndpointToUpdateBehavior() throws Exception {
        final String newBehavior = "{ \"rule\": \"credentialStuffingActive\", \"behavior\": \"LOG\"}";
        this.mockMvc.perform(post("/actuator/behaviors")
                .content(newBehavior)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());

        this.mockMvc.perform(get("/actuator/behaviors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rules", MapContentMatchers.hasEntry("credentialStuffingActive", Behaviors.LOG.name())));
    }

    private RequestPostProcessor remoteAddress(String ip) {
        return request -> {
            request.setRemoteAddr(ip);
            return request;
        };
    }

    private String randomIp() {
        final Random random = new Random();

        return Joiner.on('.').join(
                random.nextInt(256),
                random.nextInt(256),
                random.nextInt(256),
                random.nextInt(256)
        );
    }

    private ResultActions loginSuccessfully() throws Exception {
        return this.mockMvc
                .perform(formLogin().user("user").password("user").build())
                .andExpect(authenticated());
    }

    private ResultActions loginSuccessfullyWithStigma(String stigmaToken) throws Exception {
        return this.mockMvc
                .perform(formLogin().user("user").password("user").build().cookie(new Cookie(stigmaCookie, stigmaToken)))
                .andExpect(authenticated());
    }

    private ResultActions loginFailure() throws Exception {
        return this.mockMvc
                .perform(formLogin().user("user").password("bad-password").build())
                .andExpect(unauthenticated());
    }

    private ResultActions loginFailureWithStigma(String stigmaToken) throws Exception {
        return this.mockMvc
                .perform(formLogin().user("user").password("bad-password").build().cookie(new Cookie(stigmaCookie, stigmaToken)))
                .andExpect(unauthenticated());
    }

    private ResultMatcher isBlocked() {
        return redirectedUrl("/login?blockedError");
    }

    private ResultMatcher captchaChallenge() {
        return content().string(containsString("class=\"g-recaptcha\""));
    }

    private ResultMatcher noCaptchaChallenge() {
        return content().string(not(containsString("class=\"g-recaptcha\"")));
    }
}
