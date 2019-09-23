package eu.xword.nixer.nixerplugin.filter;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Lists;
import eu.xword.nixer.nixerplugin.filter.behavior.Behavior;
import eu.xword.nixer.nixerplugin.filter.behavior.BehaviorProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


@Order(value = Ordered.HIGHEST_PRECEDENCE + 20)
@Component
public class BehaviorExecutionFilter extends OncePerRequestFilter {

    @Autowired
    private BehaviorProvider behaviorProvider;

    private boolean dryRun;

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {

        if ("/login".equals(request.getPathInfo()) && "POST".equals(request.getMethod())) {
            Map<String, Object> metadata = prepareMetadata(request);

            final List<Behavior> behaviors = behaviorProvider.get(metadata);

            //filter/sanitize
            List<Behavior> sanitized = sanitize(behaviors);

            if (!dryRun) {
                //execute
                execute(request, response, sanitized);
            }
        }
        filterChain.doFilter(request, response);
    }

    private List<Behavior> sanitize(final List<Behavior> behaviors) {
        return behaviors.isEmpty() ? Collections.emptyList() : Lists.newArrayList(behaviors.get(0));
    }

    private void execute(final HttpServletRequest request, final HttpServletResponse response, final List<Behavior> behaviors) throws IOException {
        for (Behavior behavior : behaviors) {
            behavior.act(request, response);
        }
    }

    private Map<String, Object> prepareMetadata(final HttpServletRequest request) {
        final Enumeration<String> attributeNames = request.getAttributeNames();
        Map<String, Object> metadata = new HashMap<>();
        while (attributeNames.hasMoreElements()) {
            String key = attributeNames.nextElement();
            if (key.startsWith("nixer")) {
                metadata.put(key, request.getAttribute(key));
            }
        }
        return metadata;
    }
}
