package io.nixer.nixerplugin.core.detection.registry;

import java.time.Duration;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.nixer.nixerplugin.core.detection.events.UserAgentFailedLoginOverThresholdEvent;
import org.springframework.context.ApplicationListener;

/**
 * Stores user-agents which are over threshold for login failures
 */
public class UserAgentOverLoginThresholdRegistry implements ApplicationListener<UserAgentFailedLoginOverThresholdEvent> {

    private Duration expirationTime = Duration.ofMinutes(5);

    private final Cache<String, String> userAgentsOverThreshold = CacheBuilder.newBuilder()
            .expireAfterWrite(expirationTime)
            .build();


    public boolean contains(final String userAgent) {
        return userAgentsOverThreshold.getIfPresent(userAgent) != null;
    }

    @Override
    public void onApplicationEvent(final UserAgentFailedLoginOverThresholdEvent event) {
        userAgentsOverThreshold.put(event.getUserAgent(), event.getUserAgent());
    }

    public void setExpirationTime(final Duration expirationTime) {
        this.expirationTime = expirationTime;
    }
}
