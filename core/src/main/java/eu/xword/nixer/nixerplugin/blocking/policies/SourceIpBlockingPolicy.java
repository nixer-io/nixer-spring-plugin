package eu.xword.nixer.nixerplugin.blocking.policies;

import java.io.IOException;
import java.time.Duration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.MoreObjects;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import eu.xword.nixer.nixerplugin.blocking.events.BlockSourceIPEvent;
import org.springframework.context.ApplicationListener;

public class SourceIpBlockingPolicy extends BlockingPolicy implements ApplicationListener<BlockSourceIPEvent> {

    private final Cache<String, String> blockedIps = CacheBuilder.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(5))
            .build();

    private final MitigationStrategy mitigationStrategy;

    public SourceIpBlockingPolicy(final MitigationStrategy mitigationStrategy) {
        this.mitigationStrategy = MoreObjects.firstNonNull(mitigationStrategy, new ObserveOnlyMitigationStrategy());
    }

    @Override
    public void onApplicationEvent(final BlockSourceIPEvent event) {
        // TODO block for time
        blockedIps.put(event.getIp(), event.getIp());
    }

    @Override
    public void apply(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final String ip = blockedIps.getIfPresent(request.getRemoteAddr());
        if (ip != null) {
            mitigationStrategy.handle(request, response);
        }
    }
}
