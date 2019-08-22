package eu.xword.nixer.nixerplugin.captcha;

import java.util.List;

public class CompositeCaptchaInterceptor implements CaptchaInterceptor {

    private List<CaptchaInterceptor> interceptors;

    public CompositeCaptchaInterceptor(final List<CaptchaInterceptor> interceptors) {
        this.interceptors = interceptors;
    }

    @Override
    public void onCheck() {
        interceptors.forEach(CaptchaInterceptor::onCheck);
    }

    @Override
    public void onSuccess() {
        interceptors.forEach(CaptchaInterceptor::onSuccess);
    }

    @Override
    public void onFailure() {
        interceptors.forEach(CaptchaInterceptor::onFailure);
    }
}
