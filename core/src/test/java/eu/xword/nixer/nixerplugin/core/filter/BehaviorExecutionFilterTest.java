package eu.xword.nixer.nixerplugin.core.filter;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.ImmutableList;
import eu.xword.nixer.nixerplugin.core.filter.behavior.Behavior;
import eu.xword.nixer.nixerplugin.core.filter.behavior.BehaviorProvider;
import eu.xword.nixer.nixerplugin.core.filter.behavior.Facts;
import eu.xword.nixer.nixerplugin.core.registry.GlobalCredentialStuffingRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static eu.xword.nixer.nixerplugin.core.filter.BehaviorExecutionFilterTest.StubBehavior.committingBehavior;
import static eu.xword.nixer.nixerplugin.core.filter.BehaviorExecutionFilterTest.StubBehavior.nonCommittingBehavior;
import static eu.xword.nixer.nixerplugin.core.filter.RequestAugmentation.GLOBAL_CREDENTIAL_STUFFING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@ExtendWith(MockitoExtension.class)
class BehaviorExecutionFilterTest {

    private BehaviorExecutionFilter behaviorExecutionFilter;

    @Mock
    private GlobalCredentialStuffingRegistry globalCredentialStuffingRegistry;

    @Mock
    private BehaviorProvider behaviorProvider;

    @Mock
    private MockFilterChain filterChain;

    private MockHttpServletResponse response = new MockHttpServletResponse();

    @Captor
    private ArgumentCaptor<Facts> factsArgumentCaptor;

    @BeforeEach
    void setup() {
        behaviorExecutionFilter = new BehaviorExecutionFilter(behaviorProvider, globalCredentialStuffingRegistry);
    }

    @Test
    void should_build_facts() throws ServletException, IOException {
        MockHttpServletRequest request = loginRequest();
        request.setAttribute("nixer.flag", true);
        request.setAttribute("other.flag", true);

        given(globalCredentialStuffingRegistry.isCredentialStuffingActive()).willReturn(true);

        execute(request);

        verify(behaviorProvider).get(factsArgumentCaptor.capture());

        Facts facts = factsArgumentCaptor.getValue();
        assertNotNull(facts);
        assertEquals(facts.getFact("nixer.flag"), true);
        assertEquals(facts.getFact(GLOBAL_CREDENTIAL_STUFFING), true);
    }

    @Test
    void should_execute_behavior() throws ServletException, IOException {
        MockHttpServletRequest request = loginRequest();
        Behavior behavior = committingBehavior();
        givenBehaviors(behavior);

        execute(request);

        verifyExecuted(behavior);
    }

    @Test
    void should_not_execute_in_dry_run() throws ServletException, IOException {
        MockHttpServletRequest request = loginRequest();
        behaviorExecutionFilter.setDryRun(true);
        Behavior behavior = committingBehavior();
        givenBehaviors(behavior);

        execute(request);

        verifyNotExecuted(behavior);
    }

    @Test
    void should_skip_processing_for_not_matching_request() throws ServletException, IOException {
        MockHttpServletRequest request = logoutRequest();

        execute(request);

        verifyZeroInteractions(behaviorProvider);
    }

    @Test
    void should_execute_both_behaviors() throws ServletException, IOException {
        MockHttpServletRequest request = loginRequest();
        Behavior committingBehavior = committingBehavior();
        Behavior nonCommittingBehavior = nonCommittingBehavior();
        Behavior otherNonCommittingBehavior = nonCommittingBehavior();
        givenBehaviors(nonCommittingBehavior, committingBehavior, otherNonCommittingBehavior);

        execute(request);

        verifyExecuted(committingBehavior);
        verifyExecuted(nonCommittingBehavior);
        verifyExecuted(otherNonCommittingBehavior);
    }

    private void execute(final MockHttpServletRequest request) throws ServletException, IOException {
        behaviorExecutionFilter.doFilterInternal(request, response, filterChain);
    }

    private void givenBehaviors(final Behavior... behaviors) {
        given(behaviorProvider.get(Mockito.any(Facts.class)))
                .willReturn(ImmutableList.copyOf(behaviors));
    }

    @Test
    void should_execute_only_first_committing_behavior() throws ServletException, IOException {
        MockHttpServletRequest request = loginRequest();
        Behavior firstCommittingBehavior = committingBehavior();
        Behavior secondCommittingBehavior = committingBehavior();
        givenBehaviors(firstCommittingBehavior, secondCommittingBehavior);

        execute(request);

        verifyExecuted(firstCommittingBehavior);
        verifyNotExecuted(secondCommittingBehavior);
    }

    @Test
    void should_stop_executing_filter_chain_on_committing_behavior() throws ServletException, IOException {
        MockHttpServletRequest request = loginRequest();
        Behavior committingBehavior = committingBehavior();
        givenBehaviors(committingBehavior);

        execute(request);

        verifyExecuted(committingBehavior);
        verifyFilterChainIgnored(request);
    }

    private void verifyFilterChainIgnored(final MockHttpServletRequest request) throws IOException, ServletException {
        verify(filterChain, never()).doFilter(request, response);
    }

    private MockHttpServletRequest loginRequest() {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/login");
        request.setMethod("POST");
        return request;
    }

    private MockHttpServletRequest logoutRequest() {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/logout");
        request.setMethod("POST");
        return request;
    }

    private void verifyExecuted(Behavior behavior) {
        StubBehavior stubBehavior = (StubBehavior) behavior;
        assertTrue(stubBehavior.executed);
    }

    private void verifyNotExecuted(Behavior behavior) {
        StubBehavior stubBehavior = (StubBehavior) behavior;
        assertFalse(stubBehavior.executed);
    }

    static class StubBehavior implements Behavior {

        boolean isCommitting;
        boolean executed;

        StubBehavior(boolean isCommitting) {
            this.isCommitting = isCommitting;
        }

        @Override
        public void act(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
            executed = true;
        }

        @Override
        public boolean isCommitting() {
            return isCommitting;
        }

        @Override
        public String name() {
            return "stub";
        }

        static Behavior committingBehavior() {
            return new StubBehavior(true);
        }

        static Behavior nonCommittingBehavior() {
            return new StubBehavior(false);
        }

    }
}