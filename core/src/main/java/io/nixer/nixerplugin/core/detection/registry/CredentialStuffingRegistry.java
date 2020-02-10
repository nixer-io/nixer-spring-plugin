package io.nixer.nixerplugin.core.detection.registry;

import java.time.Duration;

import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import io.nixer.nixerplugin.core.detection.events.GlobalCredentialStuffingEvent;
import io.nixer.nixerplugin.core.detection.rules.ratio.FailedLoginRatioRule;
import io.nixer.nixerplugin.core.util.NowSource;
import org.springframework.context.ApplicationListener;

/**
 * Provides information about global credential stuffing attacks detected.
 * In stand-alone plugin that is defined by failed-to-successful login ratio, see {@link FailedLoginRatioRule}.
 */
public class CredentialStuffingRegistry implements ApplicationListener<GlobalCredentialStuffingEvent> {

    private final Duration credentialStuffingDuration;

    private final NowSource nowSource;

    private final RangeSet<Long> credentialStuffingWindows = TreeRangeSet.create();

    public CredentialStuffingRegistry(final Duration credentialStuffingDuration, final NowSource nowSource) {
        this.credentialStuffingDuration = credentialStuffingDuration;
        this.nowSource = nowSource;
    }

    public boolean hasHappenDuringCredentialStuffing(long timestamp) {
        return credentialStuffingWindows.contains(timestamp);
    }

    public boolean isCredentialStuffingActive() {
        return hasHappenDuringCredentialStuffing(nowSource.currentTimeMillis());
    }

    @Override
    public void onApplicationEvent(final GlobalCredentialStuffingEvent event) {
        if (!isCredentialStuffingActive()) {
            credentialStuffingWindows.add(Range.open(event.getTimestamp(), event.getTimestamp() + credentialStuffingDuration.toMillis()));
        }
    }
}
