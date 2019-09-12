package eu.xword.nixer.nixerplugin.blocking.policies;

import java.time.Duration;
import javax.servlet.http.HttpServletRequest;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import eu.xword.nixer.nixerplugin.UserUtils;
import eu.xword.nixer.nixerplugin.blocking.events.LockUserEvent;
import org.springframework.context.ApplicationListener;

public class UsernameFilter extends NixerFilter implements ApplicationListener<LockUserEvent> {

    private final Cache<String, String> blockedUsers = CacheBuilder.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(5))
            .build();

    public boolean applies(final HttpServletRequest request) {
        final String username = UserUtils.extractUsername(request);
        final String value = blockedUsers.getIfPresent(username);

        return value != null;
    }

    @Override
    public void onApplicationEvent(final LockUserEvent event) {
        // TODO block for time
        blockedUsers.put(event.getUsername(), event.getUsername());
    }
}
