package io.nixer.nixerplugin.core.detection.registry;

import java.time.Duration;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.nixer.nixerplugin.core.detection.events.IpFailedLoginOverThresholdEvent;
import org.springframework.context.ApplicationListener;

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
        ipOverThreshold.put(event.getIp(), event.getIp());
    }

    public void setExpirationTime(final Duration expirationTime) {
        this.expirationTime = expirationTime;
    }
}
