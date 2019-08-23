package eu.xword.nixer.nixerplugin.captcha.strategy;

/**
 * Contains constant definitions for simple implementations of {@link CaptchaStrategy}.
 */
public class CaptchaStrategies {

    /**
     * Strategy that always challenges
     */
    public static final CaptchaStrategy ALWAYS = new CaptchaStrategy() {
        @Override
        public boolean challenge() {
            return Boolean.TRUE;
        }

        @Override
        public boolean verifyChallenge() {
            return Boolean.TRUE;
        }

        @Override
        public String name() {
            return "ALWAYS";
        }
    };

    /**
     * Strategy that never challenges
     */
    public static final CaptchaStrategy NEVER = new CaptchaStrategy() {
        @Override
        public boolean challenge() {
            return Boolean.FALSE;
        }

        @Override
        public boolean verifyChallenge() {
            return Boolean.FALSE;
        }

        @Override
        public String name() {
            return "NEVER";
        }
    };

}
