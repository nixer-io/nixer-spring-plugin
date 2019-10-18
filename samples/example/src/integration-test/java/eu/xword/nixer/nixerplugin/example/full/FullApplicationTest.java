package eu.xword.nixer.nixerplugin.example.full;

import eu.xword.nixer.nixerplugin.captcha.recaptcha.RecaptchaClientStub;
import eu.xword.nixer.nixerplugin.captcha.security.CaptchaChecker;
import eu.xword.nixer.nixerplugin.captcha.security.CaptchaCondition;
import eu.xword.nixer.nixerplugin.detection.config.AnomalyRulesProperties;
import eu.xword.nixer.nixerplugin.filter.behavior.Behaviors;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.integration.test.matcher.MapContentMatchers;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.SmartRequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static eu.xword.nixer.nixerplugin.detection.config.AnomalyRulesProperties.Name.ip;
import static eu.xword.nixer.nixerplugin.detection.config.AnomalyRulesProperties.Name.useragent;
import static eu.xword.nixer.nixerplugin.example.LoginRequestBuilder.formLogin;
import static eu.xword.nixer.nixerplugin.filter.RequestAugmentation.USER_AGENT_FAILED_LOGIN_OVER_THRESHOLD;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 *
 * @author Joe Grandja
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(FullApplicationTest.TestConfig.class)
public class FullApplicationTest {

    private static final String LOGIN_PAGE = "/login";
    private static final String CAPTCHA_PARAM = "g-recaptcha-response";
    private static final String FAKE_USER_AGENT = "user-agent";
    private static final String GOOD_CAPTCHA = "good-captcha";
    private static final String BAD_CAPTCHA = "bad-captcha";
    private static final String BLACKLISTED_IP_V6 = "5555:5555:5555:5555:5555:5555:5555:5555";
    private static final String BLACKLISTED_IP_V4 = "5.5.5.5";

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

        this.mockMvc.perform(get(LOGIN_PAGE).session(session).session(session))
            .andExpect(status().isOk())
            .andExpect(captchaChallenge());

        this.mockMvc.perform(formLogin().user("user").password("user").captcha(GOOD_CAPTCHA).build()
                .session(session))
                .andExpect(authenticated());

        final String newDeviceIp = "192.168.1.1";
        this.mockMvc.perform(get(LOGIN_PAGE)
                .session(new MockHttpSession())
                .with(remoteAddress(newDeviceIp)))
            .andExpect(status().isOk())
            .andExpect(noCaptchaChallenge());
    }

    @Test
    void shouldSetFlatThatUserAgentOverThreshold() throws Exception {
        // enable session controlled mode
        this.captchaChecker.setCaptchaCondition(CaptchaCondition.SESSION_CONTROLLED);

        // @formatter:on
        for (int i = 0; i < ruleProperties.getFailedLoginThreshold().get(useragent).getThreshold() + 1; i++) {
            this.mockMvc.perform(formLogin().user("user").password("guess").build()
                    .header(USER_AGENT, FAKE_USER_AGENT))
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
    void shouldSetFlagForPwnPass() throws  Exception {

        final String notPwnedPassword = "not-pwned-password";
        this.mockMvc.perform(formLogin().user("user").password(notPwnedPassword).build())
                .andExpect(request().attribute("nixer.pwned.password", nullValue()));

        // using password from pwned-database
        final String pwnPassword = "foobar1";
        this.mockMvc.perform(formLogin().user("user").password(pwnPassword).build())
                .andExpect(request().attribute("nixer.pwned.password", true));
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
        final Counter successLoginCounter = meterRegistry.get("login")
                .tag("result", "success")
                .counter();
        double successLoginCount = successLoginCounter.count();

        loginSuccessfully();

        assertThat(successLoginCounter.count()).isEqualTo(successLoginCount + 1);
    }

    @Test
    void shouldReportLoginFailedMetric() throws Exception {
        final Counter failLoginCounter = meterRegistry.get("login")
                .tag("result", "failed")
                .counter();
        double failedLoginCount = failLoginCounter.count();

        loginFailure();

        assertThat(failLoginCounter.count()).isEqualTo(failedLoginCount + 1);
    }

    @Test
    void shouldFailLoginFromBlacklistedIpv4() throws Exception {
        // @formatter:off
        this.mockMvc.perform(
                formLogin().user("user").password("user")
                .build().with(remoteAddress(BLACKLISTED_IP_V4)))
                .andExpect(isBlocked());
        // @formatter:on
    }

    @Test
    void shouldFailLoginFromBlacklistedIpv6() throws Exception {
        // @formatter:off
        this.mockMvc.perform(
                formLogin().user("user").password("user")
                .build().with(remoteAddress(BLACKLISTED_IP_V6)))
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

    private void loginSuccessfully() throws Exception {
        // @formatter:off
        this.mockMvc.perform(formLogin().user("user").password("user").build())
                .andExpect(authenticated());
        // @formatter:on
    }

    private void loginFailure() throws Exception {
        // @formatter:off
        this.mockMvc.perform(formLogin().user("user").password("bad-password").build())
                .andExpect(unauthenticated());
        // @formatter:on
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
