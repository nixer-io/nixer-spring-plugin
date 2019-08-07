package eu.xword.nixer.nixerplugin.blocking.policies;

import java.util.concurrent.atomic.AtomicBoolean;

import eu.xword.nixer.nixerplugin.blocking.events.ActivateCaptchaEvent;
import eu.xword.nixer.nixerplugin.captcha.strategy.CaptchaStrategy;
import org.springframework.context.ApplicationListener;

public class AutomaticCaptchaStrategy implements CaptchaStrategy, ApplicationListener<ActivateCaptchaEvent> {

    //TODO think how to rewrite. Currently it has to me spring bean to work
    private final AtomicBoolean captchaEnabled = new AtomicBoolean(true);

    @Override
    public boolean applies() {
        return captchaEnabled.get();
    }

    @Override
    public String name() {
        return "AUTOMATIC";
    }

    @Override
    public void onApplicationEvent(final ActivateCaptchaEvent event) {
        //TODO schedule unlock after sometime/expect DropCaptcha event
        captchaEnabled.set(true);
    }
}
