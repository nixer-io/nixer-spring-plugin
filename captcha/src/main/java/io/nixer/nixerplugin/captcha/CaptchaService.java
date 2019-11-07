package io.nixer.nixerplugin.captcha;

import io.nixer.nixerplugin.captcha.error.CaptchaException;

/**
 * Allows to verify correctness of captcha response.
 *
 */
public interface CaptchaService {

    /** Verifies captcha response
     *
     * @param captcha captcha response supplied by user.
     * @throws CaptchaException if incorrect captcha was supplied or verification failed.
     */
    void verifyResponse(final String captcha) throws CaptchaException;
}
