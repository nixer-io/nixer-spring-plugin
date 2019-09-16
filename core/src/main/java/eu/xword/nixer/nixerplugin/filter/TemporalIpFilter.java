package eu.xword.nixer.nixerplugin.filter;

import java.time.Duration;
import javax.servlet.http.HttpServletRequest;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import eu.xword.nixer.nixerplugin.events.BlockSourceIpEvent;
import org.springframework.context.ApplicationListener;

public class TemporalIpFilter extends NixerFilter implements ApplicationListener<BlockSourceIpEvent> {

    private final Cache<String, String> blockedIps = CacheBuilder.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(5))
            .build();


    @Override
    public void onApplicationEvent(final BlockSourceIpEvent event) {
        // TODO block for time
        blockedIps.put(event.getIp(), event.getIp());
    }

    public boolean applies(final HttpServletRequest request) {
        final String ip = request.getRemoteAddr();
        final String ipMatch = blockedIps.getIfPresent(ip);
        return ipMatch != null;
    }
}
