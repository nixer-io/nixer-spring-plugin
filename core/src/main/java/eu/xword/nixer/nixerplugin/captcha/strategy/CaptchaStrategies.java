package eu.xword.nixer.nixerplugin.captcha.strategy;

public class CaptchaStrategies {

    /**
     * Results with captcha challenge
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
     * Results with captcha challenge
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
