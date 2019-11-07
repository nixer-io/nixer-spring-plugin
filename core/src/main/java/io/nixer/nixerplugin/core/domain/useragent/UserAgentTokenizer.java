package io.nixer.nixerplugin.core.domain.useragent;

import com.google.common.base.Charsets;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

/**
 * This class tokenize user-agent string by truncating its length and hashing result.
 */
public class UserAgentTokenizer {

    private static final int DEFAULT_TRUNCATION = 1000;

    private final HashFunction hashing = Hashing.sha256();
    private final int cutOff;

    public UserAgentTokenizer(final int cutOff) {
        this.cutOff = cutOff;
    }

    public String tokenize(final String userAgent) {
        if (userAgent == null) {
            return null;
        }
        final String trimmed = truncate(userAgent);
        return hashing.hashString(trimmed, Charsets.UTF_8).toString();
    }

    private String truncate(final String userAgent) {
        return userAgent.length() <= cutOff ? userAgent : userAgent.substring(cutOff);
    }

    public static UserAgentTokenizer sha1Tokenizer() {
        return sha1Tokenizer(DEFAULT_TRUNCATION);
    }

    public static UserAgentTokenizer sha1Tokenizer(int truncateTo) {
        return new UserAgentTokenizer(truncateTo);
    }

}
