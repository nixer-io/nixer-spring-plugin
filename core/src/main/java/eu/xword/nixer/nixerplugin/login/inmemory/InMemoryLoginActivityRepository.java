package eu.xword.nixer.nixerplugin.login.inmemory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

import eu.xword.nixer.nixerplugin.login.LoginActivityRepository;
import eu.xword.nixer.nixerplugin.login.LoginContext;
import eu.xword.nixer.nixerplugin.login.LoginFailureType;
import eu.xword.nixer.nixerplugin.login.LoginResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryLoginActivityRepository implements LoginActivityRepository {

    private final Counter failedLoginByIp = new Counter<>(LoginContext::getIpAddress);
    private final Counter unknownUserByIp = new Counter<>(LoginContext::getIpAddress);
    private final Counter failedLoginByUsername = new Counter<>(LoginContext::getUsername);

    //TODO make thresholds configurable
    private static final int LOGIN_FAILED_BY_IP_THRESHOLD = 50;
    private static final int LOGIN_FAILED_BY_USER_THRESHOLD = 5;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void reportLoginActivity(final LoginResult result, final LoginContext context) {
        result
                .onSuccess(success -> {
                    failedLoginByIp.reset(context);
                    //TODO check username for null
                    if (context.getUsername() != null) {
                        failedLoginByUsername.reset(context);
                    }
                })
                .onFailure(failure -> {
                    final Integer failedByIp = failedLoginByIp.increment(context);
//                    if (failedByIp > 2) {
//                        eventPublisher.publishEvent(new ActivateCaptchaEvent());
//                    }
                    // TODO make sure we don't trigger block event twice
//                    if (failedByIp > LOGIN_FAILED_BY_IP_THRESHOLD) {
//                        eventPublisher.publishEvent(new BlockSourceIPEvent(context.getIpAddress()));
//                    }
                    if (failure.getFailureType() == LoginFailureType.UNKNOWN_USER) {
                        unknownUserByIp.increment(context);
                    }
                    if (context.getUsername() != null) {
                        final Integer failedByUser = failedLoginByUsername.increment(context);
//                        if (failedByUser > LOGIN_FAILED_BY_USER_THRESHOLD) {
//                            eventPublisher.publishEvent(new LockUserEvent(context.getUsername()));
//                        }
                    }
                });
    }

    public static class Counter<T> {
        private static final BiFunction<Object, Integer, Integer> ZERO_OR_INCRY = (key, count) -> count == null ? 0 : count + 1;

        private final ConcurrentHashMap<T, Integer> counts = new ConcurrentHashMap<>();
        private final Function<LoginContext, T> keyFunction;

        public Counter(final Function<LoginContext, T> keyFunction) {
            this.keyFunction = keyFunction;
        }

        public Integer increment(LoginContext key) {
            return counts.compute(keyFunction.apply(key), ZERO_OR_INCRY);
        }

        public Integer reset(LoginContext key) {
            return counts.remove(keyFunction.apply(key));
        }
    }
}
