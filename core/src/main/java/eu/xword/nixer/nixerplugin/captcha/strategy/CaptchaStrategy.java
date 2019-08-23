package eu.xword.nixer.nixerplugin.captcha.strategy;

/**
 * Encapsulates decision whether user should be challenged with captcha
 */
public interface CaptchaStrategy {

    /**
     *
     * @return true if user should be challenged with captcha
     */
    boolean challenge();

    /**
     *
     * @return true if captcha response should be verified
     */
    boolean verifyChallenge();

    String name();
}
