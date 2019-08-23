package eu.xword.nixer.nixerplugin.captcha;

import eu.xword.nixer.nixerplugin.captcha.error.RecaptchaException;

/**
 * Allows to verify correctness of captcha response.
 *
 */
public interface CaptchaService {

    /** Verifies captcha response
     *
     * @param captcha captcha response supplied by user.
     * @throws {@link RecaptchaException} if incorrect captcha was supplied or verification failed.
     */
    void processResponse(String captcha) throws RecaptchaException;
}
