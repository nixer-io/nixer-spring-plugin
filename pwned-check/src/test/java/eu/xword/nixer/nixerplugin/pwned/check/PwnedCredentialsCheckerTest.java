package eu.xword.nixer.nixerplugin.pwned.check;

import com.google.common.base.Strings;
import eu.xword.nixer.bloom.BloomFilterCheck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * Created on 11/10/2019.
 *
 * @author gcwiak
 */
@ExtendWith(MockitoExtension.class)
class PwnedCredentialsCheckerTest {

    private static final String SAMPLE_PASSWORD = "samplePassword";
    private static final int MAX_PASSWORD_LENGTH = 50;

    @Mock
    BloomFilterCheck pwnedFilter;

    private PwnedCredentialsChecker pwnedCredentialsChecker;

    @BeforeEach
    void setUp() {
        pwnedCredentialsChecker = new PwnedCredentialsChecker(pwnedFilter, MAX_PASSWORD_LENGTH);
    }

    @Test
    void should_delegate_check_to_the_filter() {
        // given
        given(pwnedFilter.test(SAMPLE_PASSWORD)).willReturn(true);

        // when
        final boolean result = pwnedCredentialsChecker.isPasswordPwned(SAMPLE_PASSWORD);

        // then
        assertThat(result).isTrue();
        verify(pwnedFilter).test(SAMPLE_PASSWORD);
    }

    @Test
    void should_skip_check_for_missing_password() {
        // when
        final boolean result = pwnedCredentialsChecker.isPasswordPwned(null);

        // then
        assertThat(result).isFalse();
        verifyZeroInteractions(pwnedFilter);
    }

    @Test
    void should_skip_check_for_too_long_password() {
        // given
        final String tooLongPassword = Strings.repeat("a", MAX_PASSWORD_LENGTH + 1);

        // when
        final boolean result = pwnedCredentialsChecker.isPasswordPwned(tooLongPassword);

        // then
        assertThat(result).isFalse();
        verifyZeroInteractions(pwnedFilter);
    }
}
