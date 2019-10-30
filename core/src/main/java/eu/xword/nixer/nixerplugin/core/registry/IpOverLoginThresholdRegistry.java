package eu.xword.nixer.nixerplugin.core.registry;

import java.time.Duration;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import eu.xword.nixer.nixerplugin.core.events.IpFailedLoginOverThresholdEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Stores ip which are over threshold for login failures
 */
public class IpOverLoginThresholdRegistry implements ApplicationListener<IpFailedLoginOverThresholdEvent> {

    private Duration expirationTime = Duration.ofMinutes(5);

    private final Cache<String, String> ipOverThreshold = CacheBuilder.newBuilder()
            .expireAfterWrite(expirationTime)
            .build();

    public boolean contains(String ip) {
        return this.ipOverThreshold.getIfPresent(ip) != null;
    }

    @Override
    public void onApplicationEvent(final IpFailedLoginOverThresholdEvent event) {
        // TODO block for time
        ipOverThreshold.put(event.getIp(), event.getIp());
    }

    public void setExpirationTime(final Duration expirationTime) {
        this.expirationTime = expirationTime;
    }
}
