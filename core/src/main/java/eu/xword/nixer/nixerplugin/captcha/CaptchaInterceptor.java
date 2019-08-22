package eu.xword.nixer.nixerplugin.captcha;

public interface CaptchaInterceptor {

    void onCheck();

    void onSuccess();

    void onFailure();
}
