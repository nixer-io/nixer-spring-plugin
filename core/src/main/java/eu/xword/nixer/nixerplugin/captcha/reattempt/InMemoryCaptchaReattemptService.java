package eu.xword.nixer.nixerplugin.captcha.reattempt;

import java.time.Duration;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import eu.xword.nixer.nixerplugin.captcha.CaptchaInterceptor;
import org.springframework.security.authentication.LockedException;

public class InMemoryCaptchaReattemptService implements CaptchaInterceptor {

    private final int maxAttempts;
    private final Duration blockingDuration;
    private final IdentityCreator identityCreator;

    private LoadingCache<String, Integer> captchaCache;

    public InMemoryCaptchaReattemptService(int maxAttempts, Duration blockingDuration, IdentityCreator identityCreator) {
        // TODO make config
        this.maxAttempts = maxAttempts;
        this.blockingDuration = blockingDuration;
        this.identityCreator = identityCreator;
        this.captchaCache = CacheBuilder.newBuilder()
                .expireAfterWrite(this.blockingDuration)
                .build(new CacheLoader<String, Integer>() {
                    @Override
                    public Integer load(final String key) throws Exception {
                        return 0;
                    }
                });
    }

    public boolean shouldBlock() {
        final String key = key();
        final Integer attempts = captchaCache.getIfPresent(key);
        return attempts != null && attempts > maxAttempts;
    }

    @Override
    public void onCheck() {
        if (shouldBlock()) {
            // TODO use other exception
            throw new LockedException("Exceeded number of failed captchas");
        }
    }

    @Override
    public void onSuccess() {
        final String key = key();
        captchaCache.invalidate(key);
    }

    @Override
    public void onFailure() {
        final String key = key();
        final Integer failedAttempts = captchaCache.getUnchecked(key);
        captchaCache.put(key, failedAttempts + 1);
    }

    protected String key() {
        return identityCreator.key();
    }

}
