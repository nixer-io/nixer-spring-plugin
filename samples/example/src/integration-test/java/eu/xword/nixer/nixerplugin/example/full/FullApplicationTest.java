package eu.xword.nixer.nixerplugin.example.full;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.xword.nixer.nixerplugin.captcha.recaptcha.RecaptchaClientStub;
import eu.xword.nixer.nixerplugin.captcha.security.CaptchaChecker;
import eu.xword.nixer.nixerplugin.captcha.strategy.AutomaticCaptchaStrategy;
import eu.xword.nixer.nixerplugin.captcha.strategy.CaptchaStrategies;
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
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static eu.xword.nixer.nixerplugin.example.CaptchaRequestBuilder.from;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 *
 * @author Joe Grandja
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(FullApplicationTest.TestConfig.class)
public class FullApplicationTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CaptchaChecker captchaChecker;

    @Autowired
    private AutomaticCaptchaStrategy automaticCaptchaStrategy;

    @Autowired
    private RecaptchaClientStub recaptchaClientStub;


    @TestConfiguration
    public static class TestConfig {
        @Bean
        @Primary
        public RecaptchaClientStub recaptchaClientStub() {
            return new RecaptchaClientStub();
        }
    }

    @BeforeEach
    public void setup() {
        this.captchaChecker.setCaptchaStrategy(CaptchaStrategies.NEVER);

        recaptchaClientStub.recordValidCaptcha("good-captcha");
        recaptchaClientStub.recordInvalidCaptcha("bad-captcha");
    }

//    @AfterEach
//    public void teardown() {
//        recaptchaV2Service.setCaptchaClient(new FakeRecaptchaClient());
//    }

    @Test
    public void loginUser() throws Exception {
        // @formatter:off
        this.mockMvc.perform(formLogin().user("user").password("user"))
                .andExpect(authenticated());
        // @formatter:on
    }

    @Test
    public void returnCaptcha() throws Exception {
        //enable captcha
        this.captchaChecker.setCaptchaStrategy(CaptchaStrategies.ALWAYS);

        // @formatter:off
        this.mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("class=\"g-recaptcha\"")));
        // @formatter:on
    }

    @Test
    public void shouldActivateCaptcha() throws Exception {
        // enable automatic mode
        this.captchaChecker.setCaptchaStrategy(automaticCaptchaStrategy);

        // @formatter:on
        this.mockMvc.perform(formLogin().user("user").password("guess")).andExpect(unauthenticated());
        this.mockMvc.perform(formLogin().user("user").password("guess")).andExpect(unauthenticated());
        this.mockMvc.perform(formLogin().user("user").password("guess")).andExpect(unauthenticated());
        this.mockMvc.perform(formLogin().user("user").password("guess")).andExpect(unauthenticated());
        // @formatter:off

        this.mockMvc.perform(get("/login"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("class=\"g-recaptcha\"")));

        this.mockMvc.perform(
                from(formLogin().user("user").password("user"))
                        .withCaptcha("good-captcha"))
                .andExpect(authenticated());
    }

    @Test
    public void loginWithCaptcha() throws  Exception {
        //enable captcha
        this.captchaChecker.setCaptchaStrategy(CaptchaStrategies.ALWAYS);

        this.mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("class=\"g-recaptcha\"")));

        this.mockMvc.perform(
                from(formLogin().user("user").password("user"))
                        .withCaptcha("good-captcha"))
                .andExpect(authenticated());
    }

    @Test
    public void loginFailedWithCaptcha() throws  Exception {
        //enable captcha
        this.captchaChecker.setCaptchaStrategy(CaptchaStrategies.ALWAYS);

        this.mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("class=\"g-recaptcha\"")));

        this.mockMvc.perform(
                from(formLogin().user("user").password("user"))
                        .withCaptcha("bad-captcha"))
                .andExpect(unauthenticated());
    }

    @Test
    public void blockIpForTimeIfToManyCaptchaFailed() throws  Exception {
        //enable captcha
        this.captchaChecker.setCaptchaStrategy(CaptchaStrategies.ALWAYS);

        this.mockMvc.perform(from(formLogin().user("user").password("user")).withCaptcha("bad-captcha"))
                .andExpect(redirectedUrl("/login?error"));
        this.mockMvc.perform(from(formLogin().user("user").password("user")).withCaptcha("bad-captcha"))
                .andExpect(redirectedUrl("/login?error"));
        this.mockMvc.perform(from(formLogin().user("user").password("user")).withCaptcha("bad-captcha"))
                .andExpect(redirectedUrl("/login?error"));
        this.mockMvc.perform(from(formLogin().user("user").password("user")).withCaptcha("bad-captcha"))
                .andExpect(redirectedUrl("/login?error"));
        this.mockMvc.perform(from(formLogin().user("user").password("user")).withCaptcha("bad-captcha"))
                .andExpect(redirectedUrl("/login?error=LOCKED"));
    }

    @Test
    public void loginUserAccessProtected() throws Exception {
        // @formatter:off
        MvcResult mvcResult = this.mockMvc.perform(formLogin().user("user").password("user"))
                .andExpect(authenticated()).andReturn();
        // @formatter:on

        MockHttpSession httpSession = (MockHttpSession) mvcResult.getRequest().getSession(false);

        // @formatter:off
        this.mockMvc.perform(get("/").session(httpSession))
                .andExpect(status().isOk());
        // @formatter:on
    }

    @Test
    public void captchaEndpointToReturnCurrentStrategy() throws Exception {
        this.mockMvc.perform(get("/actuator/captcha"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.strategy", is("NEVER")));
    }

    @Test
    public void captchaEndpointToUpdateStrategy() throws Exception {
        final String newStrategy = "{ \"strategy\": \"ALWAYS\"}";
        this.mockMvc.perform(post("/actuator/captcha")
                .content(newStrategy)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());

        this.mockMvc.perform(get("/actuator/captcha"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.strategy", is("ALWAYS")));
    }
}
