package eu.xword.nixer.bloom.check;


import java.util.stream.Stream;

import eu.xword.nixer.bloom.NotHexStringException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

/**
 * Created on 11/10/2019.
 *
 * @author gcwiak
 */
class NoHashingTest {

    private NoHashing strategy = new NoHashing();

    @Test
    void should_convert_hex_string_to_bytes() {
        // given
        String hexString = "CBFDAC6008";

        // when
        final byte[] result = strategy.convertToBytes(hexString);

        // then
        assertThat(result).containsExactly(0xcb, 0xfd, 0xac, 0x60, 0x08);
    }

    @ParameterizedTest
    @MethodSource("notHexStrings")
    void should_fail_on_not_hex_string(String notHexString) {
        // when
        final Throwable throwable = catchThrowable(() -> strategy.convertToBytes(notHexString));

        // then
        assertThat(throwable)
                .isInstanceOf(NotHexStringException.class)
                .hasMessageContaining(String.valueOf(notHexString));
    }

    static Stream<String> notHexStrings() {
        return Stream.of(
                "CBF",
                "not-hex",
                "",
                " ",
                null
        );
    }

}