package io.nixer.nixerplugin.core.detection.filter.login;

import io.nixer.nixerplugin.core.detection.filter.RequestMetadata;
import io.nixer.nixerplugin.core.detection.registry.UserAgentOverLoginThresholdRegistry;
import io.nixer.nixerplugin.core.domain.useragent.UserAgentTokenizer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;

import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserAgentFailedLoginOverThresholdFilterTest {

    private static final String USER_AGENT_VALUE = "user-agent-value";
    private static final String USER_AGENT_TOKEN = "user-agent-token";

    @InjectMocks
    UserAgentFailedLoginOverThresholdFilter filter;

    @Mock
    UserAgentTokenizer userAgentTokenizer;

    @Mock
    UserAgentOverLoginThresholdRegistry userAgentOverLoginThresholdRegistry;

    @Test
    void should_mark_request_basing_on_user_agent() {
        // given
        given(userAgentTokenizer.tokenize(USER_AGENT_VALUE)).willReturn(USER_AGENT_TOKEN);
        given(userAgentOverLoginThresholdRegistry.contains(USER_AGENT_TOKEN)).willReturn(true);

        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setServletPath("/login");
        request.addHeader(HttpHeaders.USER_AGENT, USER_AGENT_VALUE);

        // when
        filter.apply(request);

        // then
        assertThat(request.getAttribute(RequestMetadata.USER_AGENT_FAILED_LOGIN_OVER_THRESHOLD)).isEqualTo(TRUE);
    }
}