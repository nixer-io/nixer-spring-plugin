package eu.xword.nixer.nixerplugin.captcha;

public interface CaptchaServiceFactory {

    CaptchaService createCaptchaService(String action);
}
