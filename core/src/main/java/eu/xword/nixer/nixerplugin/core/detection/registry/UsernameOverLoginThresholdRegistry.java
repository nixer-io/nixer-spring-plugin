package eu.xword.nixer.nixerplugin.core.detection.registry;

import java.time.Duration;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import eu.xword.nixer.nixerplugin.core.detection.events.UsernameFailedLoginOverThresholdEvent;
import org.springframework.context.ApplicationListener;

/**
 * Stores username which are over threshold for login failures
 */
public class UsernameOverLoginThresholdRegistry implements ApplicationListener<UsernameFailedLoginOverThresholdEvent> {

    private Duration expirationTime = Duration.ofMinutes(5);

    private final Cache<String, String> usernameOverThreshold = CacheBuilder.newBuilder()
            .expireAfterWrite(expirationTime)
            .build();


    public boolean contains(final String username) {
        return usernameOverThreshold.getIfPresent(username) != null;
    }

    @Override
    public void onApplicationEvent(final UsernameFailedLoginOverThresholdEvent event) {
        // TODO block for time
        usernameOverThreshold.put(event.getUsername(), event.getUsername());
    }

    public void setExpirationTime(final Duration expirationTime) {
        this.expirationTime = expirationTime;
    }
}
