package eu.xword.nixer.nixerplugin.captcha.strategy;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

public class StrategyRegistry {

    private Map<String, CaptchaStrategy> mapping = new HashMap<>();

    public StrategyRegistry() {
        registerStrategy(CaptchaStrategies.ALWAYS);
        registerStrategy(CaptchaStrategies.NEVER);
    }

    public void registerStrategy(CaptchaStrategy value) {
        mapping.put(value.name(), value);
    }

    public CaptchaStrategy valueOf(String strategy) {
        return mapping.get(strategy);
    }

}
