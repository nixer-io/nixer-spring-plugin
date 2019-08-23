package eu.xword.nixer.nixerplugin.captcha.error;

//TODO to be used for retries. Do we need it ?
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
