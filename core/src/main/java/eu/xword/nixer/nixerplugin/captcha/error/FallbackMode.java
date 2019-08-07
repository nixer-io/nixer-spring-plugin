package eu.xword.nixer.nixerplugin.captcha.error;

public enum FallbackMode {
    ACCEPT {
        @Override
        public void handle(final RecaptchaServiceException failure) {
            // NOP
        }
    },
    FAIL {
        public void handle(final RecaptchaServiceException failure) {
            throw failure;
        }
    };

    public abstract void handle(final RecaptchaServiceException failure);
}
