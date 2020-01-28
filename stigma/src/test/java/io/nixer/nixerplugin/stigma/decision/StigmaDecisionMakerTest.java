package io.nixer.nixerplugin.stigma.decision;

import java.time.Instant;

import io.nixer.nixerplugin.stigma.domain.RawStigmaToken;
import io.nixer.nixerplugin.stigma.domain.Stigma;
import io.nixer.nixerplugin.stigma.domain.StigmaDetails;
import io.nixer.nixerplugin.stigma.token.create.StigmaTokenFactory;
import io.nixer.nixerplugin.stigma.token.read.StigmaExtractor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static io.nixer.nixerplugin.stigma.domain.StigmaStatus.ACTIVE;
import static io.nixer.nixerplugin.stigma.decision.StigmaEvent.TOKEN_BAD_LOGIN_FAIL;
import static io.nixer.nixerplugin.stigma.decision.StigmaEvent.TOKEN_BAD_LOGIN_SUCCESS;
import static io.nixer.nixerplugin.stigma.decision.StigmaEvent.TOKEN_GOOD_LOGIN_FAIL;
import static io.nixer.nixerplugin.stigma.decision.StigmaEvent.TOKEN_GOOD_LOGIN_SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;

/**
 * Created on 2019-04-29.
 *
 * @author gcwiak
 */
@ExtendWith(MockitoExtension.class)
class StigmaDecisionMakerTest {

    // TODO verify metrics when implemented

    private static final Stigma STIGMA = new Stigma("stigma-value");
    private static final StigmaDetails STIGMA_DETAILS = new StigmaDetails(STIGMA, ACTIVE, Instant.parse("2020-01-21T10:25:43.511Z"));
    private static final RawStigmaToken STIGMA_TOKEN = new RawStigmaToken("valid-token");

    private static final Stigma REFRESHED_STIGMA = new Stigma("refreshed-stigma-value");
    private static final StigmaDetails REFRESHED_STIGMA_DETAILS = new StigmaDetails(REFRESHED_STIGMA, ACTIVE, Instant.parse("2020-01-22T11:26:45.512Z"));
    private static final RawStigmaToken REFRESHED_STIGMA_TOKEN = new RawStigmaToken("refreshed-token");

    @Mock
    private StigmaExtractor stigmaExtractor;

    @Mock
    private StigmaService stigmaService;

    @Mock
    private StigmaValidator stigmaValidator;

    @Mock
    private StigmaTokenFactory stigmaTokenFactory;

    @InjectMocks
    private StigmaDecisionMaker decisionMaker;

    @BeforeEach
    void setUp() {
        lenient().when(stigmaService.getNewStigma()).thenReturn(REFRESHED_STIGMA_DETAILS);
        lenient().when(stigmaTokenFactory.getToken(REFRESHED_STIGMA)).thenReturn(REFRESHED_STIGMA_TOKEN);
    }

    @Test
    void should_make_decision_on_login_success_and_valid_stigma() {
        // given
        given(stigmaExtractor.extractStigma(STIGMA_TOKEN)).willReturn(STIGMA);
        given(stigmaService.findStigmaDetails(STIGMA)).willReturn(STIGMA_DETAILS);
        given(stigmaValidator.isValid(STIGMA_DETAILS)).willReturn(true);

        // when
        final StigmaDecision result = decisionMaker.onLoginSuccess(STIGMA_TOKEN);

        // then
        assertThat(result).isEqualTo(new StigmaDecision(STIGMA_TOKEN, TOKEN_GOOD_LOGIN_SUCCESS));
        //        verify(stigmaMetricsService).rememberStigmaActionType(TOKEN_GOOD_LOGIN_SUCCESS);
    }

    @Test
    void should_make_decision_on_login_success_and_invalid_token() {
        // given
        given(stigmaExtractor.extractStigma(STIGMA_TOKEN)).willReturn(null);

        // when
        final StigmaDecision result = decisionMaker.onLoginSuccess(STIGMA_TOKEN);

        // then
        assertThat(result).isEqualTo(new StigmaDecision(REFRESHED_STIGMA_TOKEN, TOKEN_BAD_LOGIN_SUCCESS));
        //        verify(stigmaMetricsService).rememberStigmaActionType(TOKEN_BAD_LOGIN_SUCCESS);
    }

    @Test
    void should_make_decision_on_login_success_and_not_active_stigma() {
        // given
        given(stigmaExtractor.extractStigma(STIGMA_TOKEN)).willReturn(STIGMA);
        given(stigmaService.findStigmaDetails(STIGMA)).willReturn(STIGMA_DETAILS);
        given(stigmaValidator.isValid(STIGMA_DETAILS)).willReturn(false);

        // when
        final StigmaDecision result = decisionMaker.onLoginSuccess(STIGMA_TOKEN);

        // then
        assertThat(result).isEqualTo(new StigmaDecision(REFRESHED_STIGMA_TOKEN, TOKEN_BAD_LOGIN_SUCCESS));
        //        verify(stigmaMetricsService).rememberStigmaActionType(TOKEN_BAD_LOGIN_SUCCESS);
    }

    @Test
    void should_make_decision_on_login_failure_and_valid_token() {
        // given
        given(stigmaExtractor.extractStigma(STIGMA_TOKEN)).willReturn(STIGMA);
        given(stigmaService.findStigmaDetails(STIGMA)).willReturn(STIGMA_DETAILS);
        given(stigmaValidator.isValid(STIGMA_DETAILS)).willReturn(true);

        // when
        final StigmaDecision result = decisionMaker.onLoginFail(STIGMA_TOKEN);

        // then
        assertThat(result).isEqualTo(new StigmaDecision(REFRESHED_STIGMA_TOKEN, TOKEN_GOOD_LOGIN_FAIL));
        //        verify(stigmaMetricsService).rememberStigmaActionType(TOKEN_GOOD_LOGIN_FAIL);
    }

    @Test
    void should_make_decision_on_login_failure_and_invalid_token() {
        // given
        given(stigmaExtractor.extractStigma(STIGMA_TOKEN)).willReturn(null);

        // when
        final StigmaDecision result = decisionMaker.onLoginFail(STIGMA_TOKEN);

        // then
        assertThat(result).isEqualTo(new StigmaDecision(REFRESHED_STIGMA_TOKEN, TOKEN_BAD_LOGIN_FAIL));
        //        verify(stigmaMetricsService).rememberStigmaActionType(TOKEN_BAD_LOGIN_FAIL);
    }

    @Test
    void should_make_decision_on_login_failure_and_not_active_stigma() {
        // given
        given(stigmaExtractor.extractStigma(STIGMA_TOKEN)).willReturn(STIGMA);
        given(stigmaService.findStigmaDetails(STIGMA)).willReturn(STIGMA_DETAILS);
        given(stigmaValidator.isValid(STIGMA_DETAILS)).willReturn(false);

        // when
        final StigmaDecision result = decisionMaker.onLoginFail(STIGMA_TOKEN);

        // then
        assertThat(result).isEqualTo(new StigmaDecision(REFRESHED_STIGMA_TOKEN, TOKEN_BAD_LOGIN_FAIL));
        //        verify(stigmaMetricsService).rememberStigmaActionType(TOKEN_BAD_LOGIN_FAIL);
    }
}
