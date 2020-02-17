package io.nixer.nixerplugin.stigma.crypto;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;

import com.google.common.base.Joiner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

/**
 * Created on 2019-05-29.
 *
 * @author gcwiak
 */
class KeysLoaderTest {

    private static final String KEY_1 =
            "{\"kty\":\"oct\",\"alg\":\"dir\",\"k\":\"2zUFSf5c0m_E1MjSSiS5iwZ9lzQY-9sqQXHnU1KqW8w\",\"kid\":\"key-id-1\"}";

    private static final String KEY_2 =
            "{\"kty\":\"oct\",\"alg\":\"dir\",\"k\":\"Bsd2_f0XUx7mBscwqLvnwDk-cic-3UmMst65Ws1IUBc\",\"kid\":\"key-id-2\"}";

    private static final String KEY_3 =
            "{\"kty\":\"oct\",\"alg\":\"dir\",\"k\":\"h5x9QYWzzQtQTZXjd22S-ZCMoL29OgdsRoNiLqsMSB0\",\"kid\":\"key-id-3\"}";

    @TempDir
    Path temporaryFolder;

    File encryptionFile;
    File decryptionFile;

    @BeforeEach
    void setUp() {
        encryptionFile = temporaryFolder.resolve("st-enc-jwk.json").toFile();
        decryptionFile = temporaryFolder.resolve("st-dec-jwk.json").toFile();
    }

    @Test
    void should_load_encryption_and_decryption_keys() throws IOException, ParseException {
        // given
        givenKeysInFile(encryptionFile, KEY_1);
        givenKeysInFile(decryptionFile, KEY_1, KEY_2);

        // when
        final KeysLoader keysLoader = KeysLoader.load(encryptionFile, decryptionFile);
        final JWK encryptionKey = keysLoader.getEncryptionKey();
        final ImmutableJWKSet decryptionKeySet = keysLoader.getDecryptionKeySet();

        // then
        assertThat(encryptionKey).isEqualTo(JWK.parse(KEY_1));
        assertThat(decryptionKeySet.getJWKSet().getKeys()).containsExactly(JWK.parse(KEY_1), JWK.parse(KEY_2));
    }

    @Test
    void should_throw_exception_when_decryption_keys_do_not_include_the_encryption_key() throws IOException {
        // given
        givenKeysInFile(encryptionFile, KEY_1);
        givenKeysInFile(decryptionFile, KEY_2, KEY_3);

        // when
        final Throwable throwable = catchThrowable(() -> KeysLoader.load(encryptionFile, decryptionFile));

        // then
        assertThat(throwable).hasMessage("Decryption keys must include the encryption key.");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"kty\":\"oct\",\"alg\":\"dir\",\"k\":\"cTPHbpsKiBNTAf9tREHZPyFCnEJBOpqExuVedV89oSI\"}",
            "{\"kty\":\"oct\",\"alg\":\"dir\",\"k\":\"cTPHbpsKiBNTAf9tREHZPyFCnEJBOpqExuVedV89oSI\",\"kid\": null}",
            "{\"kty\":\"oct\",\"alg\":\"dir\",\"k\":\"cTPHbpsKiBNTAf9tREHZPyFCnEJBOpqExuVedV89oSI\",\"kid\":\"\"}",
            "{\"kty\":\"oct\",\"alg\":\"dir\",\"k\":\"cTPHbpsKiBNTAf9tREHZPyFCnEJBOpqExuVedV89oSI\",\"kid\":\" \"}",
            "{\"kty\":\"oct\",\"alg\":\"dir\",\"k\":\"cTPHbpsKiBNTAf9tREHZPyFCnEJBOpqExuVedV89oSI\",\"kid\":\"  \"}"
    })
    void should_throw_exception_when_encryption_key_does_not_have_key_id(final String jwkWithoutKid) throws IOException {
        // given
        givenKeysInFile(encryptionFile, jwkWithoutKid);
        givenKeysInFile(decryptionFile, jwkWithoutKid, KEY_2);

        // when
        final Throwable throwable = catchThrowable(() -> KeysLoader.load(encryptionFile, decryptionFile));

        // then
        assertThat(throwable).hasMessage("Encryption key must include key ID (kid).");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"kty\":\"oct\",\"alg\":\"dir\",\"k\":\"cTPHbpsKiBNTAf9tREHZPyFCnEJBOpqExuVedV89oSI\"}",
            "{\"kty\":\"oct\",\"alg\":\"dir\",\"k\":\"cTPHbpsKiBNTAf9tREHZPyFCnEJBOpqExuVedV89oSI\",\"kid\": null}",
            "{\"kty\":\"oct\",\"alg\":\"dir\",\"k\":\"cTPHbpsKiBNTAf9tREHZPyFCnEJBOpqExuVedV89oSI\",\"kid\":\"\"}",
            "{\"kty\":\"oct\",\"alg\":\"dir\",\"k\":\"cTPHbpsKiBNTAf9tREHZPyFCnEJBOpqExuVedV89oSI\",\"kid\":\" \"}",
            "{\"kty\":\"oct\",\"alg\":\"dir\",\"k\":\"cTPHbpsKiBNTAf9tREHZPyFCnEJBOpqExuVedV89oSI\",\"kid\":\"  \"}"
    })
    void should_throw_exception_when_decryption_keys_do_not_have_key_id(final String jwkWithoutKid) throws IOException {
        // given
        givenKeysInFile(encryptionFile, KEY_1);
        givenKeysInFile(decryptionFile, KEY_1, jwkWithoutKid);

        // when
        final Throwable throwable = catchThrowable(() -> KeysLoader.load(encryptionFile, decryptionFile));

        // then
        assertThat(throwable).hasMessage("All decryption keys must include key ID (kid).");
    }

    @Test
    void should_throw_exception_when_decryption_keys_have_duplicated_key_ids() throws IOException {
        // given
        givenKeysInFile(encryptionFile, KEY_1);
        givenKeysInFile(decryptionFile, KEY_1,
                "{\"kty\":\"oct\",\"alg\":\"dir\",\"k\":\"Bsd2_f0XUx7mBscwqLvnwDk-cic-3UmMst65Ws1IUBc\",\"kid\":\"key-id-1\"}");

        // when
        final Throwable throwable = catchThrowable(() -> KeysLoader.load(encryptionFile, decryptionFile));

        // then
        assertThat(throwable).hasMessage("Decryption keys must have unique key IDs.");
    }

    private static void givenKeysInFile(final File targetFile, final String... keys) throws IOException {
        final String fileContent = "{\"keys\":[" + Joiner.on(",").join(keys) + "]}";
        Files.write(targetFile.toPath(), fileContent.getBytes(StandardCharsets.UTF_8));
    }
}
