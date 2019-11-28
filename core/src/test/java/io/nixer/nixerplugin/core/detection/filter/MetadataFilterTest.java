package io.nixer.nixerplugin.core.detection.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;

class MetadataFilterTest {

    @Test
    void shouldContinueFilterProcessingInChainOnException() throws IOException, ServletException {
        MetadataFilter filter = new ExceptionThrowingFilter();

        final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/login");
        final MockHttpServletResponse response = new MockHttpServletResponse();
        final FilterChain filterChain = Mockito.mock(FilterChain.class);

        assertDoesNotThrow(() -> filter.doFilter(request, response, filterChain));

        verify(filterChain).doFilter(request, response);
    }

    private static class ExceptionThrowingFilter extends MetadataFilter {

        @Override
        protected void apply(final HttpServletRequest request) {
            throw new RuntimeException();
        }
    }
}