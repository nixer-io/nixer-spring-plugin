package eu.xword.nixer.nixerplugin.blocking.policies;

import java.util.concurrent.atomic.AtomicLong;

import eu.xword.nixer.nixerplugin.blocking.events.ActivateCaptchaEvent;
import eu.xword.nixer.nixerplugin.captcha.strategy.CaptchaStrategy;
import org.springframework.context.ApplicationListener;

// TODO should we move it to eu.xword.nixer.nixerplugin.captcha.strategy
//TODO think how to rewrite. Currently it has to me spring bean to work
public class AutomaticCaptchaStrategy implements CaptchaStrategy, ApplicationListener<ActivateCaptchaEvent> {

    private final AtomicLong enableCaptchaAfter = new AtomicLong(Long.MAX_VALUE);

    @Override
    public boolean applies(long sessionCreationTime) {
        return sessionCreationTime > enableCaptchaAfter.get();
    }

    @Override
    public String name() {
        return "AUTOMATIC";
    }

    @Override
    public void onApplicationEvent(final ActivateCaptchaEvent event) {
        //TODO schedule unlock after sometime/expect DropCaptcha event
        enableCaptchaAfter.set(event.getTimestamp());
    }
}
