package io.nixer.example.stigma;

import java.util.List;
import javax.servlet.http.Cookie;

import io.nixer.nixerplugin.stigma.domain.StigmaStatus;
import io.nixer.nixerplugin.stigma.domain.StigmaDetails;
import io.nixer.nixerplugin.stigma.storage.jdbc.StigmasJdbcDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.export.influx.InfluxMetricsExportAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static io.nixer.example.LoginRequestBuilder.formLogin;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;

/**
 * Created on 09/12/2019.
 *
 * @author Grzegorz Cwiak (gcwiak)
 */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@EnableAutoConfiguration(exclude = InfluxMetricsExportAutoConfiguration.class) // TODO find better way for excluding influx, e.g. profiles
@Transactional
class StigmaTest {

    // TODO verify Stigma metrics

    @Autowired
    private MockMvc mockMvc;

    @Value("${nixer.stigma.cookieName}")
    private String stigmaCookie;

    @Autowired
    private StigmasJdbcDAO stigmaDAO;

    @BeforeEach
    void setUp() {
        assertThat(stigmaDAO.getAll()).isEmpty();
    }

    @Test
    void shouldAssignStigmaAfterSuccessfulLogin() throws Exception {
        final String stigmaToken = loginSuccessfullyAndGetStigma();

        final List<StigmaDetails> stigmasAfterFirstLogin = stigmaDAO.getAll();
        assertThat(stigmasAfterFirstLogin).hasSize(1)
                .extracting(StigmaDetails::getStatus).containsExactly(StigmaStatus.ACTIVE);

        // subsequent successful login with valid stigma does not require stigma refresh
        loginSuccessfullyWithStigma(stigmaToken)
                .andExpect(cookie().doesNotExist(stigmaCookie));
        assertThat(stigmaDAO.getAll()).isEqualTo(stigmasAfterFirstLogin);
    }

    @Test
    void shouldRefreshValidStigmaAfterSubsequentLoginFailure() throws Exception {
        final String stigmaToken = loginSuccessfullyAndGetStigma();

        final List<StigmaDetails> stigmasAfterFirstLogin = stigmaDAO.getAll();
        assertThat(stigmasAfterFirstLogin).hasSize(1)
                .extracting(StigmaDetails::getStatus).containsExactly(StigmaStatus.ACTIVE);

        // subsequent successful login with valid stigma does not require stigma refresh
        loginSuccessfullyWithStigma(stigmaToken)
                .andExpect(cookie().doesNotExist(stigmaCookie));

        assertThat(stigmaDAO.getAll()).isEqualTo(stigmasAfterFirstLogin);

        final String refreshedStigmaToken = loginFailureWithStigma(stigmaToken)
                .andExpect(cookie().exists(stigmaCookie))
                .andReturn().getResponse().getCookie(stigmaCookie).getValue();

        assertThat(refreshedStigmaToken)
                .isNotBlank()
                .isNotEqualTo(stigmaToken);

        assertThat(stigmaDAO.getAll()).hasSize(2)
                .extracting(StigmaDetails::getStatus).containsExactly(StigmaStatus.REVOKED, StigmaStatus.ACTIVE);
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

        assertThat(stigmaDAO.getAll()).hasSize(2)
                .extracting(StigmaDetails::getStatus).containsExactly(StigmaStatus.REVOKED, StigmaStatus.ACTIVE);
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

        assertThat(stigmaDAO.getAll()).hasSize(1)
                .extracting(StigmaDetails::getStatus).containsExactly(StigmaStatus.ACTIVE);
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

        assertThat(stigmaDAO.getAll()).hasSize(1)
                .extracting(StigmaDetails::getStatus).containsExactly(StigmaStatus.ACTIVE);
    }

    private String loginSuccessfullyAndGetStigma() throws Exception {
        return loginSuccessfully()
                .andExpect(cookie().exists(stigmaCookie))
                .andReturn().getResponse().getCookie(stigmaCookie).getValue();
    }

    private ResultActions loginSuccessfully() throws Exception {
        return this.mockMvc
                .perform(formLogin().user("user").password("user").build())
                .andExpect(authenticated());
    }

    private ResultActions loginFailure() throws Exception {
        return this.mockMvc
                .perform(formLogin().user("user").password("bad-password").build())
                .andExpect(unauthenticated());
    }

    private ResultActions loginSuccessfullyWithStigma(String stigmaToken) throws Exception {
        return this.mockMvc
                .perform(formLogin().user("user").password("user").build().cookie(new Cookie(stigmaCookie, stigmaToken)))
                .andExpect(authenticated());
    }

    private ResultActions loginFailureWithStigma(String stigmaToken) throws Exception {
        return this.mockMvc
                .perform(formLogin().user("user").password("bad-password").build().cookie(new Cookie(stigmaCookie, stigmaToken)))
                .andExpect(unauthenticated());
    }
}
