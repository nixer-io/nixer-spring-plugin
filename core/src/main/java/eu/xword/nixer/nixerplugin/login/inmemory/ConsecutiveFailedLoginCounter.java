package eu.xword.nixer.nixerplugin.login.inmemory;

import java.time.Clock;
import java.time.Duration;

import eu.xword.nixer.nixerplugin.login.LoginContext;
import eu.xword.nixer.nixerplugin.login.LoginResult;
import eu.xword.nixer.nixerplugin.login.counts.IpCountStore;
import eu.xword.nixer.nixerplugin.login.counts.LoginCounter;
import org.springframework.util.Assert;

/**
 * This counter tracks consecutive failed login per ip. Successful login resets counter.
 */
public class ConsecutiveFailedLoginCounter implements IpCountStore, LoginCounter {

    private final RollingCounter failureCounter;

    private ConsecutiveFailedLoginCounter(RollingCounter rollingCounter) {
        Assert.notNull(rollingCounter, "RollingCounter must not be null");
        failureCounter = rollingCounter;
    }

    @Override
    public int failedLoginByIp(final String ip) {
        return failureCounter.get(ip);
    }

    @Override
    public void onLogin(final LoginResult result, final LoginContext context) {
        //todo handle nulls
        final String ipAddress = context.getIpAddress();

        result
                .onFailure(it -> failureCounter.add(ipAddress))
                .onSuccess(it -> failureCounter.remove(ipAddress));
    }

    public static ConsecutiveFailedLoginCounter create(final Duration windowSize) {
        return new ConsecutiveFailedLoginCounter(new RollingCounter(windowSize));
    }

    public static ConsecutiveFailedLoginCounter create(final Duration windowSize, Clock clock) {
        final RollingCounter rollingCounter = new RollingCounter(windowSize);
        rollingCounter.setClock(clock);

        return new ConsecutiveFailedLoginCounter(rollingCounter);
    }
}
