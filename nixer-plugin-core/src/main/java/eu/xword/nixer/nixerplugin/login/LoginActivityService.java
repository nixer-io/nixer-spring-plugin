package eu.xword.nixer.nixerplugin.login;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

import eu.xword.nixer.nixerplugin.blocking.events.BlockSourceIPEvent;
import eu.xword.nixer.nixerplugin.blocking.events.LockUserEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class LoginActivityService {

    //TODO make thresholds configurable
    private static final int LOGIN_FAILED_BY_IP_THRESHOLD = 50;
    private static final int LOGIN_FAILED_BY_USER_THRESHOLD = 5;
    private static final BiFunction<String, Integer, Integer> ZERO_OR_INCRY = (key, count) -> count == null ? 0 : count + 1;

    private ConcurrentHashMap<String, Integer> loginAttemptsByIp = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Integer> loginAttemptsByUsername = new ConcurrentHashMap<>();

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public void record(final LoginResult loginResult, final LoginContext context) {
        //TODO extract keeping track of stats to dedicated place
        loginResult
                .onSuccess(success -> {
                    loginAttemptsByIp.remove(context.getIpAddress());
                    //TODO check username for null
                    if (context.getUsername() != null) {
                        loginAttemptsByUsername.remove(context.getUsername());
                    }
                })
                .onFailure(failure -> {
                    final Integer failedByIp = loginAttemptsByIp.compute(context.getIpAddress(), ZERO_OR_INCRY);
                    // TODO make sure we don't trigger block event twice
                    if (failedByIp > LOGIN_FAILED_BY_IP_THRESHOLD) {
                        eventPublisher.publishEvent(new BlockSourceIPEvent(context.getIpAddress()));
                    }

                    if (context.getUsername() != null) {
                        final Integer failedByUser = loginAttemptsByUsername.compute(context.getUsername(), ZERO_OR_INCRY);
                        if (failedByUser > LOGIN_FAILED_BY_USER_THRESHOLD) {
                            eventPublisher.publishEvent(new LockUserEvent(context.getUsername()));
                        }
                    }
                });
    }
}
