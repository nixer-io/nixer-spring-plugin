package eu.xword.nixer.nixerplugin.captcha.reattempt;

/**
 * Creates identity of user from contextual data.
 */
public interface IdentityCreator {
    String key();
}
