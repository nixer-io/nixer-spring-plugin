package eu.xword.nixer.nixerplugin.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.xword.nixer.nixerplugin.detection.GlobalCredentialStuffing;
import eu.xword.nixer.nixerplugin.filter.behavior.Behavior;
import eu.xword.nixer.nixerplugin.filter.behavior.BehaviorProvider;
import eu.xword.nixer.nixerplugin.filter.behavior.Facts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import static eu.xword.nixer.nixerplugin.filter.RequestAugmentation.GLOBAL_CREDENTIAL_STUFFING;


@Component
@Order(value = Ordered.HIGHEST_PRECEDENCE + 20)
public class BehaviorExecutionFilter extends OncePerRequestFilter {

    @Autowired
    private BehaviorProvider behaviorProvider;

    @Autowired
    private GlobalCredentialStuffing globalCredentialStuffing;

    private boolean dryRun;

    private RequestMatcher requestMatcher = new AntPathRequestMatcher("/login", "POST");

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {

        if (requestMatcher.matches(request)) {
            final Facts facts = prepareFacts(request);

            final List<Behavior> behaviors = behaviorProvider.get(facts);

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
        List<Behavior> result = new ArrayList<>();
        boolean gotExclusive = false;
        for (Behavior behavior : behaviors) {
            if (behavior.category() == Behavior.Category.STACKABLE) {
                result.add(behavior);
            } else if (!gotExclusive) {
                result.add(behavior);
                gotExclusive = true;
            }
        }

        return result;
    }

    private void execute(final HttpServletRequest request, final HttpServletResponse response, final List<Behavior> behaviors) throws IOException {
        for (Behavior behavior : behaviors) {
            logger.info(dryRun ? "[dryRun]" : "" + "Executing behaviour " + behavior.toString());
            if (!dryRun) {
                behavior.act(request, response);
            }
        }
    }

    private Facts prepareFacts(final HttpServletRequest request) {
        final Enumeration<String> attributeNames = request.getAttributeNames();
        Map<String, Object> metadata = new HashMap<>();
        while (attributeNames.hasMoreElements()) {
            String key = attributeNames.nextElement();
            if (key.startsWith("nixer")) {
                metadata.put(key, request.getAttribute(key));
            }
        }
        metadata.put(GLOBAL_CREDENTIAL_STUFFING, globalCredentialStuffing.isCredentialStuffingActive());

        return new Facts(metadata);
    }

    public void setDryRun(final boolean dryRun) {
        this.dryRun = dryRun;
    }

    public void setRequestMatcher(final RequestMatcher requestMatcher) {
        this.requestMatcher = requestMatcher;
    }
}
