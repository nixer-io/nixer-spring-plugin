package eu.xword.nixer.nixerplugin.detection;

import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import eu.xword.nixer.nixerplugin.blocking.events.CredentialStuffingEvent;
import org.springframework.context.ApplicationListener;

/**
 * Provides information about global credential stuffing attacks detected.
 *
 */
public class GlobalCredentialStuffing implements ApplicationListener<CredentialStuffingEvent> {

    // TODO for non-overlaping ranges such as this. BST could be used with O(log n) search time.

    private final RangeSet<Long> credentialStuffingWindows = TreeRangeSet.create();

    public boolean hasHappenDuringCredentialStuffing(long timestamp) {
        return credentialStuffingWindows.contains(timestamp);
    }

    public boolean isCredentialStuffingActive() {
        return hasHappenDuringCredentialStuffing(System.currentTimeMillis());
    }

    @Override
    public void onApplicationEvent(final CredentialStuffingEvent event) {
        //TODO schedule unlock after sometime/expect DropCaptcha event
        credentialStuffingWindows.add(Range.open(event.getTimestamp(), Long.MAX_VALUE));
    }
}
