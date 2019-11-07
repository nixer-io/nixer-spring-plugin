package eu.xword.nixer.nixerplugin.core.detection.registry;

import java.time.Duration;

import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import eu.xword.nixer.nixerplugin.core.detection.events.GlobalCredentialStuffingEvent;
import org.springframework.context.ApplicationListener;

/**
 * Provides information about global credential stuffing attacks detected.
 *
 */
public class CredentialStuffingRegistry implements ApplicationListener<GlobalCredentialStuffingEvent> {

    private final Duration credentialStuffingDuration = Duration.ofMinutes(15);

    private final RangeSet<Long> credentialStuffingWindows = TreeRangeSet.create();

    public boolean hasHappenDuringCredentialStuffing(long timestamp) {
        return credentialStuffingWindows.contains(timestamp);
    }

    public boolean isCredentialStuffingActive() {
        return hasHappenDuringCredentialStuffing(System.currentTimeMillis());
    }

    @Override
    public void onApplicationEvent(final GlobalCredentialStuffingEvent event) {
        if (!isCredentialStuffingActive()) {
            credentialStuffingWindows.add(Range.open(event.getTimestamp(), event.getTimestamp() + credentialStuffingDuration.toMillis()));
        }
    }
}
