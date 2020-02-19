package io.nixer.example.integrationTest.pwned;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.metrics.export.influx.InfluxMetricsExportAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static io.nixer.example.integrationTest.LoginRequestBuilder.formLogin;
import static io.nixer.nixerplugin.pwned.metrics.PwnedCheckCounters.METRIC_NAME;
import static io.nixer.nixerplugin.pwned.metrics.PwnedCheckCounters.NOT_PWNED_RESULT;
import static io.nixer.nixerplugin.pwned.metrics.PwnedCheckCounters.PWNED_RESULT;
import static io.nixer.nixerplugin.pwned.metrics.PwnedCheckCounters.RESULT_TAG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

/**
 * Created on 18/02/2020.
 *
 * @author Grzegorz Cwiak (gcwiak)
 */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@EnableAutoConfiguration(exclude = InfluxMetricsExportAutoConfiguration.class)
@Transactional
class PwnedTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MeterRegistry meterRegistry;

    @AfterEach
    void tearDown() throws Exception {
        mockMvc.perform(logout());
    }

    @Test
    void shouldDetectPwnedPassword() throws Exception {

        loginWithNotPwnedPassword()
                .andExpect(request().attribute("nixer.pwned.password", nullValue()));

        loginWithPwnedPassword()
                .andExpect(request().attribute("nixer.pwned.password", true));
    }

    @Test
    void shouldWritePwnedPasswordMetrics() throws Exception {
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
}
