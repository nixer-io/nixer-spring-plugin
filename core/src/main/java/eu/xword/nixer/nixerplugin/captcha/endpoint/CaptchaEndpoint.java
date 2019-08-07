package eu.xword.nixer.nixerplugin.captcha.endpoint;

import java.util.HashMap;
import java.util.Map;

import eu.xword.nixer.nixerplugin.captcha.CaptchaChecker;
import eu.xword.nixer.nixerplugin.captcha.strategy.CaptchaStrategy;
import eu.xword.nixer.nixerplugin.captcha.strategy.StrategyRegistry;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.util.Assert;

@Endpoint(id = "captcha")
public class CaptchaEndpoint {

    private final CaptchaChecker captchaChecker;

    private final StrategyRegistry strategyRegistry;

    public CaptchaEndpoint(final CaptchaChecker captchaChecker, final StrategyRegistry strategyRegistry) {
        Assert.notNull(captchaChecker, "CaptchaChecker must not be null");
        this.captchaChecker = captchaChecker;

        Assert.notNull(strategyRegistry, "StrategyRegistry must not be null");
        this.strategyRegistry = strategyRegistry;
    }

    @ReadOperation
    public Map<String, Object> strategy() {
        final CaptchaStrategy strategy = captchaChecker.getCaptchaStrategy();
        final Map<String, Object> map = new HashMap<>();
        map.put("strategy", strategy.name());
        return map;
    }

    @WriteOperation
    public void configure(String strategy) {
        final CaptchaStrategy captchaStrategy = strategyRegistry.valueOf((String) strategy);

        captchaChecker.setCaptchaStrategy(captchaStrategy);
    }

}
