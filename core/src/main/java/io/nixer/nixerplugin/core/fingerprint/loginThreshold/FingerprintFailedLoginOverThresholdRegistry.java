package io.nixer.nixerplugin.core.fingerprint.loginThreshold;

import java.time.Duration;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.context.ApplicationListener;


public class FingerprintFailedLoginOverThresholdRegistry implements ApplicationListener<FingerprintFailedLoginOverThresholdEvent> {

    private Duration expirationTime = Duration.ofMinutes(5);

    private final Cache<String, String> fingerprintFailedLoginOverThreshold = CacheBuilder.newBuilder()
            .expireAfterWrite(expirationTime)
            .build();

    public boolean contains(String fingerprint) {
        return this.fingerprintFailedLoginOverThreshold.getIfPresent(fingerprint) != null;
    }

    @Override
    public void onApplicationEvent(final FingerprintFailedLoginOverThresholdEvent event) {
        fingerprintFailedLoginOverThreshold.put(event.getFingerprint(), event.getFingerprint());
    }

    public void setExpirationTime(final Duration expirationTime) {
        this.expirationTime = expirationTime;
    }
}
