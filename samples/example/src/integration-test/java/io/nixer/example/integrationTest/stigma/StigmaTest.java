package io.nixer.example.integrationTest.stigma;

import java.util.List;
import javax.servlet.http.Cookie;

import io.nixer.nixerplugin.stigma.StigmaConstants;
import io.nixer.nixerplugin.stigma.domain.RawStigmaToken;
import io.nixer.nixerplugin.stigma.domain.StigmaDetails;
import io.nixer.nixerplugin.stigma.domain.StigmaStatus;
import io.nixer.nixerplugin.stigma.storage.jdbc.StigmasJdbcDAO;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.transaction.annotation.Transactional;

import static io.nixer.example.integrationTest.LoginRequestBuilder.formLogin;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

/**
 * Created on 09/12/2019.
 *
 * @author Grzegorz Cwiak (gcwiak)
 */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@EnableAutoConfiguration(exclude = InfluxMetricsExportAutoConfiguration.class)
@Transactional
class StigmaTest {

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

    @AfterEach
    void tearDown() throws Exception {
        mockMvc.perform(logout());
    }

    @Test
    void should_assign_stigma_after_successful_login() throws Exception {
        final RawStigmaToken stigmaToken = loginSuccessfully();

        final List<StigmaDetails> stigmasAfterFirstLogin = stigmaDAO.getAll();
        assertThat(stigmasAfterFirstLogin).hasSize(1)
                .extracting(StigmaDetails::getStatus).containsExactly(StigmaStatus.ACTIVE);

        // subsequent successful login with valid stigma does not require stigma refresh
        loginSuccessfully(stigmaToken)
                .andExpect(cookie().doesNotExist(stigmaCookie));

        assertThat(stigmaDAO.getAll()).isEqualTo(stigmasAfterFirstLogin);
    }

    @Test
    void should_refresh_valid_stigma_after_subsequent_login_failure() throws Exception {
        final RawStigmaToken stigmaToken = loginSuccessfully();

        final List<StigmaDetails> stigmasAfterFirstLogin = stigmaDAO.getAll();
        assertThat(stigmasAfterFirstLogin).hasSize(1)
                .extracting(StigmaDetails::getStatus).containsExactly(StigmaStatus.ACTIVE);

        // subsequent successful login with valid stigma does not require stigma refresh
        loginSuccessfully(stigmaToken)
                .andExpect(cookie().doesNotExist(stigmaCookie));

        assertThat(stigmaDAO.getAll()).isEqualTo(stigmasAfterFirstLogin);

        final RawStigmaToken refreshedStigmaToken = loginFailure(stigmaToken);

        assertThat(refreshedStigmaToken).isNotEqualTo(stigmaToken);
        assertThat(stigmaDAO.getAll()).hasSize(2)
                .extracting(StigmaDetails::getStatus).containsExactly(StigmaStatus.REVOKED, StigmaStatus.ACTIVE);
    }

    @Test
    void should_refresh_stigma_after_failed_login() throws Exception {
        final RawStigmaToken firstStigmaToken = loginFailure();

        final RawStigmaToken secondStigmaToken = loginFailure(firstStigmaToken);

        assertThat(secondStigmaToken).isNotEqualTo(firstStigmaToken);
        assertThat(stigmaDAO.getAll()).hasSize(2)
                .extracting(StigmaDetails::getStatus).containsExactly(StigmaStatus.REVOKED, StigmaStatus.ACTIVE);
    }

    @Test
    void should_refresh_invalid_stigma_after_successful_login() throws Exception {
        final RawStigmaToken invalidStigmaToken = new RawStigmaToken("invalid-stigma-token");

        final RawStigmaToken newStigmaToken = getStigmaToken(loginSuccessfully(invalidStigmaToken));

        assertThat(newStigmaToken).isNotEqualTo(invalidStigmaToken);
        assertThat(stigmaDAO.getAll()).hasSize(1)
                .extracting(StigmaDetails::getStatus).containsExactly(StigmaStatus.ACTIVE);
    }

    @Test
    void should_refresh_invalid_stigma_after_failed_login() throws Exception {
        final RawStigmaToken invalidStigmaToken = new RawStigmaToken("invalid-stigma-token");

        final RawStigmaToken newStigmaToken = loginFailure(invalidStigmaToken);

        assertThat(newStigmaToken).isNotEqualTo(invalidStigmaToken);
        assertThat(stigmaDAO.getAll()).hasSize(1)
                .extracting(StigmaDetails::getStatus).containsExactly(StigmaStatus.ACTIVE);
    }

    @Test
    void should_detect_multiple_failed_logins_with_the_same_stigma() throws Exception {
        final RawStigmaToken stigmaToken = loginFailure();
        loginFailure(stigmaToken);

        doLoginFailure(stigmaToken)
                .andExpect(request().attribute(StigmaConstants.STIGMA_METADATA_ATTRIBUTE, instanceOf(StigmaDetails.class)))
                .andExpect(isBlocked());
    }

    private static ResultMatcher isBlocked() {
        return redirectedUrl("/login?blockedError");
    }

    private RawStigmaToken loginSuccessfully() throws Exception {
        return getStigmaToken(
                this.mockMvc
                        .perform(formLogin().user("user").password("user").build())
                        .andExpect(authenticated())
        );
    }

    private ResultActions loginSuccessfully(RawStigmaToken stigmaToken) throws Exception {
        return this.mockMvc
                .perform(formLogin().user("user").password("user").build().cookie(new Cookie(stigmaCookie, stigmaToken.getValue())))
                .andExpect(authenticated());
    }

    private RawStigmaToken loginFailure(final RawStigmaToken stigmaToken) throws Exception {
        return getStigmaToken(
                this.mockMvc
                        .perform(formLogin().user("user").password("bad-password").build().cookie(new Cookie(stigmaCookie, stigmaToken.getValue())))
                        .andExpect(unauthenticated())
        );
    }

    private ResultActions doLoginFailure(final RawStigmaToken stigmaToken) throws Exception {
        return this.mockMvc
                .perform(formLogin().user("user").password("bad-password").build().cookie(new Cookie(stigmaCookie, stigmaToken.getValue())))
                .andExpect(unauthenticated());
    }

    private RawStigmaToken loginFailure() throws Exception {
        return getStigmaToken(
                this.mockMvc
                        .perform(formLogin().user("user").password("bad-password").build())
                        .andExpect(unauthenticated())
        );
    }

    private RawStigmaToken getStigmaToken(final ResultActions resultActions) throws Exception {
        final String stigmaCookieValue = resultActions
                .andExpect(cookie().exists(stigmaCookie))
                .andReturn().getResponse().getCookie(stigmaCookie).getValue();

        assertThat(stigmaCookieValue).isNotBlank();

        return new RawStigmaToken(stigmaCookieValue);
    }
}
