package eu.xword.nixer.nixerplugin.captcha.recaptcha;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableList;
import eu.xword.nixer.nixerplugin.captcha.error.CaptchaErrors;

import static eu.xword.nixer.nixerplugin.captcha.recaptcha.RecaptchaVerifyResponse.ErrorCode.InvalidResponse;

/**
 * RecaptchaClient Stub for recording responses for given captcha
 */
public class RecaptchaClientStub implements RecaptchaClient {

    private Map<String, Supplier<RecaptchaVerifyResponse>> responses = new HashMap<>();

    @Override
    public RecaptchaVerifyResponse call(final String captcha) {
        if (responses.containsKey(captcha)) {
            return responses.get(captcha).get();
        } else {
            throw new IllegalArgumentException("Unknown argument " + captcha);
        }
    }

    public void recordValidCaptcha(String captcha) {
        responses.put(captcha, () -> new RecaptchaVerifyResponse(true, "", "",
                ImmutableList.of()));
    }

    public void recordInvalidCaptcha(String captcha) {
        responses.put(captcha, () -> new RecaptchaVerifyResponse(false, "", "",
                ImmutableList.of(InvalidResponse)));
    }

    public void recordFailedCaptchaCheck(String captcha) {
        responses.put(captcha, () -> {
            throw CaptchaErrors.serviceFailure("Failed calling captcha verify", null);
        });
    }
}
