package eu.xword.nixer.nixerplugin.captcha.strategy;

public interface CaptchaStrategy {

    boolean applies();

    String name();
}
