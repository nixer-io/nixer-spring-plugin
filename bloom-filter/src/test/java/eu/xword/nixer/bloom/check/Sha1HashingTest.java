package eu.xword.nixer.bloom.check;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created on 11/10/2019.
 *
 * @author gcwiak
 */
class Sha1HashingTest {

    private Sha1Hashing strategy = new Sha1Hashing();

    @Test
    void should_compute_sha1_as_bytes() {
        // given
        String inputValue = "someValue";
        // which has the following sha1
        // echo -n someValue | shasum | cut -d " " -f 1
        // c1353b55ce4db511684b8a3b7b5c4b3d99ee9dec

        // when
        final byte[] result = strategy.convertToBytes(inputValue);

        // then
        assertThat(result).containsExactly(
                0xc1, 0x35, 0x3b, 0x55, 0xce, 0x4d, 0xb5, 0x11, 0x68, 0x4b, 0x8a, 0x3b, 0x7b, 0x5c, 0x4b, 0x3d, 0x99, 0xee, 0x9d, 0xec
        );
    }
}
