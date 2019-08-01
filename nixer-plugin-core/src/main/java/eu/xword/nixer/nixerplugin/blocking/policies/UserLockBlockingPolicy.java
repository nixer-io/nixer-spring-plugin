package eu.xword.nixer.nixerplugin.blocking.policies;

import java.io.IOException;
import java.time.Duration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.MoreObjects;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import eu.xword.nixer.nixerplugin.UserUtils;
import eu.xword.nixer.nixerplugin.blocking.events.LockUserEvent;
import org.springframework.context.ApplicationListener;

public class UserLockBlockingPolicy extends BlockingPolicy implements ApplicationListener<LockUserEvent> {

    private final Cache<String, String> blockedUsers = CacheBuilder.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(5))
            .build();

    private final MitigationStrategy mitigationStrategy;

    public UserLockBlockingPolicy(final MitigationStrategy mitigationStrategy) {
        this.mitigationStrategy = MoreObjects.firstNonNull(mitigationStrategy, new ObserveOnlyMitigationStrategy());
    }

    @Override
    public void apply(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final String username = UserUtils.extractUsername(request);
        final String value = blockedUsers.getIfPresent(username);
        if (value != null) {
            mitigationStrategy.handle(request, response);
        }
    }

    @Override
    public void onApplicationEvent(final LockUserEvent event) {
        // TODO block for time
        blockedUsers.put(event.getUsername(), event.getUsername());
    }
}
