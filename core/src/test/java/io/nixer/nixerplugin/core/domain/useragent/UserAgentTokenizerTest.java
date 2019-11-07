package io.nixer.nixerplugin.core.domain.useragent;

import org.junit.jupiter.api.Test;

import static io.nixer.nixerplugin.core.domain.useragent.UserAgentTokenizer.sha1Tokenizer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserAgentTokenizerTest {

    @Test
    void tokenize_regular_user_agent() {
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36";

        final String token = UserAgentTokenizer.sha1Tokenizer().tokenize(userAgent);

        assertEquals("9d43022f716c611ffef9990e855ea7dfb94e258f08e745f26e597a5c767cd768", token);
    }

    @Test
    void tokenize_missing_user_agent() {
        final String token = UserAgentTokenizer.sha1Tokenizer().tokenize(null);

        assertNull(token);
    }

    @Test
    void tokenize_truncated_user_agent() {
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36";
        String truncatedUserAgent = userAgent.substring(100);

        final String tokenForFull = UserAgentTokenizer.sha1Tokenizer(100).tokenize(userAgent);
        final String tokenForTruncated = UserAgentTokenizer.sha1Tokenizer(100).tokenize(truncatedUserAgent);

        assertEquals(tokenForTruncated, tokenForFull);
    }
}
