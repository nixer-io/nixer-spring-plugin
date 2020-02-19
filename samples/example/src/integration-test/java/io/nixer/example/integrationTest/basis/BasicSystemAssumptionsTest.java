package io.nixer.example.integrationTest.basis;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.nixer.nixerplugin.core.detection.filter.behavior.Behaviors;
import io.nixer.nixerplugin.core.login.metrics.LoginCounters;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.metrics.export.influx.InfluxMetricsExportAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.integration.test.matcher.MapContentMatchers;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.SmartRequestBuilder;

import static io.nixer.example.integrationTest.LoginRequestBuilder.formLogin;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude = InfluxMetricsExportAutoConfiguration.class)
class BasicSystemAssumptionsTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MeterRegistry meterRegistry;

    @AfterEach
    void tearDown() throws Exception {
        mockMvc.perform(logout());
    }

    @Test
    void shouldLoginSuccessfully() throws Exception {
        loginSuccessfully();
    }

    @Test
    void loginUserAccessProtected() throws Exception {
        final SmartRequestBuilder loginRequest = formLogin().user("user").password("user").build();
        MvcResult mvcResult = this.mockMvc.perform(loginRequest)
                .andExpect(authenticated()).andReturn();

        MockHttpSession httpSession = (MockHttpSession) mvcResult.getRequest().getSession(false);

        this.mockMvc.perform(get("/").session(httpSession))
                .andExpect(status().isOk());
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

    private Counter successLoginCounter() {
        return LoginCounters.LOGIN_SUCCESS.register(meterRegistry);
    }

    private Counter badPasswordLoginFailCounter() {
        return LoginCounters.LOGIN_FAILED_BAD_PASSWORD.register(meterRegistry);
    }

    private void loginSuccessfully() throws Exception {
        this.mockMvc
                .perform(formLogin().user("user").password("user").build())
                .andExpect(authenticated());
    }

    private void loginFailure() throws Exception {
        this.mockMvc
                .perform(formLogin().user("user").password("bad-password").build())
                .andExpect(unauthenticated());
    }
}
