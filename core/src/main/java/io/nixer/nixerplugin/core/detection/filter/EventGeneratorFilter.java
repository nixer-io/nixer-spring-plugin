package io.nixer.nixerplugin.core.detection.filter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.filter.OncePerRequestFilter;

public class EventGeneratorFilter extends OncePerRequestFilter {

    final ApplicationEventPublisher eventPublisher;

    public EventGeneratorFilter(final ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        final NixerAggregate nixerAggregate = (NixerAggregate) request.getAttribute("nixer.aggregate.data.object");

        for (Object o : nixerAggregate.getData()) {
            eventPublisher.publishEvent(o);
        }

    }

    static class NixerAggregate {
        List<Object> getData() {
            return Collections.emptyList();
        }
    }
}
