package eu.xword.nixer.nixerplugin.filter;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.ImmutableList;
import eu.xword.nixer.nixerplugin.registry.GlobalCredentialStuffingRegistry;
import eu.xword.nixer.nixerplugin.filter.behavior.Behavior;
import eu.xword.nixer.nixerplugin.filter.behavior.BehaviorProvider;
import eu.xword.nixer.nixerplugin.filter.behavior.Facts;
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

import static eu.xword.nixer.nixerplugin.filter.RequestAugmentation.GLOBAL_CREDENTIAL_STUFFING;
import static eu.xword.nixer.nixerplugin.filter.behavior.Behavior.Category.EXCLUSIVE;
import static eu.xword.nixer.nixerplugin.filter.behavior.Behavior.Category.STACKABLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@ExtendWith(MockitoExtension.class)
class BehaviorExecutionFilterTest {

    BehaviorExecutionFilter behaviorExecutionFilter;

    @Mock
    GlobalCredentialStuffingRegistry globalCredentialStuffingRegistry;

    @Mock
    BehaviorProvider behaviorProvider;

    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain filterChain = new MockFilterChain();

    @Captor
    ArgumentCaptor<Facts> factsArgumentCaptor;

    @BeforeEach
    public void setup() {
        behaviorExecutionFilter = new BehaviorExecutionFilter(behaviorProvider, globalCredentialStuffingRegistry);
    }

    @Test
    public void should_build_facts() throws ServletException, IOException {
        MockHttpServletRequest request = loginRequest();
        request.setAttribute("nixer.flag", true);
        request.setAttribute("other.flag", true);

        given(globalCredentialStuffingRegistry.isCredentialStuffingActive()).willReturn(true);

        behaviorExecutionFilter.doFilterInternal(request, response, filterChain);

        verify(behaviorProvider).get(factsArgumentCaptor.capture());

        final Facts facts = factsArgumentCaptor.getValue();
        assertNotNull(facts);
        assertEquals(facts.getFact("nixer.flag"), true);
        assertEquals(facts.getFact(GLOBAL_CREDENTIAL_STUFFING), true);
    }

    @Test
    public void should_execute_behavior() throws ServletException, IOException {
        MockHttpServletRequest request = loginRequest();

        final StubBehavior stubBehavior = new StubBehavior(EXCLUSIVE);

        given(behaviorProvider.get(Mockito.any(Facts.class)))
                .willReturn(ImmutableList.of(stubBehavior));

        behaviorExecutionFilter.doFilterInternal(request, response, filterChain);

        assertTrue(stubBehavior.executed);
    }

    @Test
    public void should_not_execute_in_dry_run() throws ServletException, IOException {
        MockHttpServletRequest request = loginRequest();
        behaviorExecutionFilter.setDryRun(true);

        final StubBehavior stubBehavior = new StubBehavior(EXCLUSIVE);

        given(behaviorProvider.get(Mockito.any(Facts.class)))
                .willReturn(ImmutableList.of(stubBehavior));

        behaviorExecutionFilter.doFilterInternal(request, response, filterChain);

        assertFalse(stubBehavior.executed);
    }

    @Test
    public void should_skip_processing_for_not_matching_request() throws ServletException, IOException {
        MockHttpServletRequest request = logoutRequest();

        behaviorExecutionFilter.doFilterInternal(request, response, filterChain);

        verifyZeroInteractions(behaviorProvider);
    }

    @Test
    public void should_execute_both_categories_of_behaviors() throws ServletException, IOException {
        MockHttpServletRequest request = loginRequest();

        final StubBehavior exclusiveBehavior = new StubBehavior(EXCLUSIVE);
        final StubBehavior stackableBehavior = new StubBehavior(STACKABLE);
        final StubBehavior otherStackableBehavior = new StubBehavior(STACKABLE);

        given(behaviorProvider.get(Mockito.any(Facts.class)))
                .willReturn(ImmutableList.of(stackableBehavior, exclusiveBehavior, otherStackableBehavior));

        behaviorExecutionFilter.doFilterInternal(request, response, filterChain);

        assertTrue(exclusiveBehavior.executed);
        assertTrue(stackableBehavior.executed);
        assertTrue(otherStackableBehavior.executed);
    }

    @Test
    public void should_execute_only_first_exclusive_behavior() throws ServletException, IOException {
        MockHttpServletRequest request = loginRequest();

        final StubBehavior firstExclusiveBehavior = new StubBehavior(EXCLUSIVE);
        final StubBehavior secondExclusiveBehavior = new StubBehavior(EXCLUSIVE);

        given(behaviorProvider.get(Mockito.any(Facts.class)))
                .willReturn(ImmutableList.of(firstExclusiveBehavior, secondExclusiveBehavior));

        behaviorExecutionFilter.doFilterInternal(request, response, filterChain);

        assertTrue(firstExclusiveBehavior.executed);
        assertFalse(secondExclusiveBehavior.executed);
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

    static class StubBehavior implements Behavior {

        Category category;
        boolean executed;

        public StubBehavior(Category category) {
            this.category = category;
        }

        @Override
        public void act(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
            executed = true;
        }

        @Override
        public Category category() {
            return category;
        }

        @Override
        public String name() {
            return "stub";
        }


    }
}