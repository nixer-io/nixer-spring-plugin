package eu.xword.nixer.nixerplugin.registry;

import java.time.Duration;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import eu.xword.nixer.nixerplugin.events.BlockSourceIpEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class BlockedIpRegistry implements ApplicationListener<BlockSourceIpEvent> {

    private Duration blockDuration = Duration.ofMinutes(5);

    public boolean isBlocked(String ip) {
        return blockedIps.getIfPresent(ip) != null;
    }

    private final Cache<String, String> blockedIps = CacheBuilder.newBuilder()
            .expireAfterWrite(blockDuration)
            .build();


    @Override
    public void onApplicationEvent(final BlockSourceIpEvent event) {
        // TODO block for time
        blockedIps.put(event.getIp(), event.getIp());
    }

    public void setBlockDuration(final Duration blockDuration) {
        this.blockDuration = blockDuration;
    }
}
