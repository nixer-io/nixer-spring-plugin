package eu.xword.nixer.nixerplugin.registry;

import java.time.Duration;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import eu.xword.nixer.nixerplugin.events.IpFailedLoginOverThresholdEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Stores ip which are over threshold for login failures
 */
@Component
public class IpFailedLoginOverThresholdRegistry implements ApplicationListener<IpFailedLoginOverThresholdEvent> {

    private Duration expirationTime = Duration.ofMinutes(5);

    private final Cache<String, String> ipStore = CacheBuilder.newBuilder()
            .expireAfterWrite(expirationTime)
            .build();

    public boolean contains(String ip) {
        return this.ipStore.getIfPresent(ip) != null;
    }

    @Override
    public void onApplicationEvent(final IpFailedLoginOverThresholdEvent event) {
        // TODO block for time
        ipStore.put(event.getIp(), event.getIp());
    }

    public void setExpirationTime(final Duration expirationTime) {
        this.expirationTime = expirationTime;
    }
}
