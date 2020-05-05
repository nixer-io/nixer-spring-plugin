package io.nixer.example.integrationTest.fingerprint;

import javax.servlet.http.Cookie;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.export.influx.InfluxMetricsExportAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static io.nixer.example.integrationTest.LoginRequestBuilder.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;

/**
 * Created on 01/05/2020.
 *
 * @author Grzegorz Cwiak
 */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@EnableAutoConfiguration(exclude = InfluxMetricsExportAutoConfiguration.class)
public class FingerprintTest {

    @Autowired
    private MockMvc mockMvc;

    @Value("${nixer.fingerprint.cookieName}")
    private String fingerprintCookie;

    @AfterEach
    void tearDown() throws Exception {
        mockMvc.perform(logout());
    }

    @Test
    void should_assign_fingerprint_cookie_when_not_already_present() throws Exception {
        final String fingerprint =
                this.mockMvc
                        .perform(formLogin().user("user").password("user").build())
                        .andExpect(authenticated())
                        .andExpect(cookie().exists(fingerprintCookie))
                        .andReturn().getResponse().getCookie(fingerprintCookie).getValue();

        this.mockMvc
                .perform(
                        formLogin().user("user").password("user").build()
                                .cookie(new Cookie(fingerprintCookie, fingerprint))
                )
                .andExpect(authenticated())
                .andExpect(cookie().doesNotExist(fingerprintCookie));
    }
}
