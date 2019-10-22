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

import eu.xword.nixer.nixerplugin.filter.behavior.Behavior;
import eu.xword.nixer.nixerplugin.filter.behavior.BehaviorProvider;
import eu.xword.nixer.nixerplugin.filter.behavior.Facts;
import eu.xword.nixer.nixerplugin.registry.GlobalCredentialStuffingRegistry;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import static eu.xword.nixer.nixerplugin.filter.RequestAugmentation.GLOBAL_CREDENTIAL_STUFFING;


@Order(value = Ordered.HIGHEST_PRECEDENCE + 20)
public class BehaviorExecutionFilter extends OncePerRequestFilter {

    private final BehaviorProvider behaviorProvider;

    private final GlobalCredentialStuffingRegistry globalCredentialStuffingRegistry;

    private boolean dryRun;

    // TODO externalize
    private final RequestMatcher requestMatcher = new AntPathRequestMatcher("/login");

    public BehaviorExecutionFilter(final BehaviorProvider behaviorProvider, final GlobalCredentialStuffingRegistry globalCredentialStuffingRegistry) {
        Assert.notNull(behaviorProvider, "BehaviorProvider must not be null");
        this.behaviorProvider = behaviorProvider;

        Assert.notNull(behaviorProvider, "GlobalCredentialStuffingRegistry must not be null");
        this.globalCredentialStuffingRegistry = globalCredentialStuffingRegistry;
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {

        if (!requestMatcher.matches(request)) {
            filterChain.doFilter(request, response);
        } else {
            final Facts facts = prepareFacts(request);

            final List<Behavior> behaviors = behaviorProvider.get(facts);

            final List<Behavior> sanitized = sanitize(behaviors);

            final boolean proceed = execute(request, response, sanitized);
            if (proceed) {
                filterChain.doFilter(request, response);
            }
        }
    }

    private List<Behavior> sanitize(final List<Behavior> behaviors) {
        List<Behavior> result = new ArrayList<>();

        boolean gotCommittingBehavior = false;
        for (Behavior behavior : behaviors) {
            if (behavior.isCommitting()) {
                if (!gotCommittingBehavior) {
                    gotCommittingBehavior = true;
                    result.add(behavior);
                }
            } else {
                result.add(behavior);
            }
        }
        return result;
    }

    private boolean execute(final HttpServletRequest request, final HttpServletResponse response, final List<Behavior> behaviors) throws IOException {
        boolean gotCommittingBehavior = false;
        for (Behavior behavior : behaviors) {
            logger.info((dryRun ? "[dry-run]" : "") + "Executing behaviour: " + behavior.name());
            if (!dryRun) {
                behavior.act(request, response);
                gotCommittingBehavior |= behavior.isCommitting();
            }
        }
        return !gotCommittingBehavior;
    }

    private Facts prepareFacts(final HttpServletRequest request) {
        final Enumeration<String> attributeNames = request.getAttributeNames();
        final Map<String, Object> metadata = new HashMap<>();
        while (attributeNames.hasMoreElements()) {
            String key = attributeNames.nextElement();
            if (key.startsWith("nixer")) {
                metadata.put(key, request.getAttribute(key));
            }
        }
        metadata.put(GLOBAL_CREDENTIAL_STUFFING, globalCredentialStuffingRegistry.isCredentialStuffingActive());

        return new Facts(metadata);
    }

    public void setDryRun(final boolean dryRun) {
        this.dryRun = dryRun;
    }
}
