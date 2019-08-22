package eu.xword.nixer.nixerplugin.captcha;

import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import eu.xword.nixer.nixerplugin.captcha.error.RecaptchaException;
import eu.xword.nixer.nixerplugin.captcha.strategy.CaptchaStrategies;
import eu.xword.nixer.nixerplugin.captcha.strategy.CaptchaStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

// TODO convert to Blocking policy
public class CaptchaChecker implements UserDetailsChecker {

    private static final String LOGIN_ACTION = "login";

    @Autowired
    private HttpServletRequest request;

    private CaptchaServiceFactory captchaServiceFactory;

    private String captchaParam;

    private CaptchaService captchaService;

    private AtomicReference<CaptchaStrategy> captchaStrategy = new AtomicReference<>(CaptchaStrategies.ALWAYS);

    public CaptchaChecker(final CaptchaServiceFactory captchaServiceFactory, final CaptchaLoginProperties captchaLoginProperties) {
        Assert.notNull(captchaServiceFactory, "CaptchaServiceFactory must not be null");
        this.captchaServiceFactory = captchaServiceFactory;

        Assert.notNull(captchaLoginProperties, "RecaptchaProperties must not be null");
        this.captchaParam = captchaLoginProperties.getParam();
    }

    @PostConstruct
    public void postInit() {
        this.captchaService = captchaServiceFactory.createCaptchaService(LOGIN_ACTION);
    }

    @Override
    public void check(final UserDetails toCheck) {
        if (shouldVerifyCaptcha()) {
            final String captchaValue = request.getParameter(captchaParam);

            try {
                captchaService.processResponse(captchaValue);
            } catch (RecaptchaException e) {
                throw new BadCaptchaException("Invalid captcha", e);
            }
        }
    }

    private long sessionCreationTimeOrZero() {
        ServletRequestAttributes attr = (ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(false);

        return session != null
                ? session.getCreationTime()
                : 0;
    }

    private boolean shouldVerifyCaptcha() {
        final long timestamp = sessionCreationTimeOrZero();

        return captchaStrategy.get().applies(timestamp);
    }

    // Needs to be public for templates to check if captcha should be displayed.
    // TODO Re-implement check so it doesn't have to be public
    public boolean shouldDisplayCaptcha() {
        return captchaStrategy.get().applies(System.currentTimeMillis()); //TODO refactor
    }

    public void setCaptchaStrategy(final CaptchaStrategy captchaStrategy) {
        Assert.notNull(captchaStrategy, "CaptchaStrategy must not be null");

        this.captchaStrategy.set(captchaStrategy);
    }

    public CaptchaStrategy getCaptchaStrategy() {
        return captchaStrategy.get();
    }
}
