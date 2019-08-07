package eu.xword.nixer.nixerplugin.captcha.strategy;

public class CaptchaStrategies {

    public static final CaptchaStrategy ALWAYS = new CaptchaStrategy() {
        @Override
        public boolean applies() {
            return Boolean.TRUE;
        }

        @Override
        public String name() {
            return "ALWAYS";
        }
    };

    public static final CaptchaStrategy NEVER = new CaptchaStrategy() {
        @Override
        public boolean applies() {
            return Boolean.FALSE;
        }

        @Override
        public String name() {
            return "NEVER";
        }
    };

}
