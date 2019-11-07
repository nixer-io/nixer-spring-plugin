package io.nixer.nixerplugin.captcha.endpoint;

import java.util.HashMap;
import java.util.Map;

import io.nixer.nixerplugin.captcha.security.CaptchaChecker;
import io.nixer.nixerplugin.captcha.security.CaptchaCondition;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.util.Assert;

/**
 * An {@link Endpoint} for exposing {@link CaptchaChecker}.
 */
@Endpoint(id = "captcha")
public class CaptchaEndpoint {

    private final CaptchaChecker captchaChecker;

    public CaptchaEndpoint(final CaptchaChecker captchaChecker) {
        Assert.notNull(captchaChecker, "CaptchaChecker must not be null");
        this.captchaChecker = captchaChecker;
    }

    @ReadOperation
    public Map<String, Object> condition() {
        final CaptchaCondition condition = captchaChecker.getCaptchaCondition();
        final Map<String, Object> map = new HashMap<>();
        map.put("condition", condition.name());
        return map;
    }

    @WriteOperation
    public void configure(String condition) {
        captchaChecker.setCaptchaCondition(CaptchaCondition.valueOf(condition));
    }

}
