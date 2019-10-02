package eu.xword.nixer.nixerplugin.captcha;

import eu.xword.nixer.nixerplugin.captcha.error.CaptchaException;

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
    void verifyResponse(String captcha) throws CaptchaException;
}
