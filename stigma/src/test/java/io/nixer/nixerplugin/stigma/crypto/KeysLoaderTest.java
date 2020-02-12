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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

/**
 * Created on 2019-05-29.
 *
 * @author gcwiak
 */
class KeysLoaderTest {

    private static final String KEY_1 =
            "{\"kty\":\"oct\",\"kid\":\"test-key-1\",\"k\":\"2zUFSf5c0m_E1MjSSiS5iwZ9lzQY-9sqQXHnU1KqW8w\",\"alg\":\"dir\"}";

    private static final String KEY_2 =
            "{\"kty\":\"oct\",\"kid\":\"test-key-2\",\"k\":\"Bsd2_f0XUx7mBscwqLvnwDk-cic-3UmMst65Ws1IUBc\",\"alg\":\"dir\"}";

    private static final String KEY_3 =
            "{\"kty\":\"oct\",\"kid\":\"test-key-3\",\"k\":\"h5x9QYWzzQtQTZXjd22S-ZCMoL29OgdsRoNiLqsMSB0\",\"alg\":\"dir\"}";

    @TempDir
    Path temporaryFolder;

    @Test
    void should_load_encryption_and_decryption_keys() throws IOException, ParseException {
        // given
        final File encryptionFile = temporaryFolder.resolve("st-enc-jwk.json").toFile();
        final File decryptionFile = temporaryFolder.resolve("st-dec-jwk.json").toFile();

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
        final File encryptionFile = temporaryFolder.resolve("st-enc-jwk.json").toFile();
        final File decryptionFile = temporaryFolder.resolve("st-dec-jwk.json").toFile();

        givenKeysInFile(encryptionFile, KEY_1);
        givenKeysInFile(decryptionFile, KEY_2, KEY_3);

        // when
        final Throwable throwable = catchThrowable(() -> KeysLoader.load(encryptionFile, decryptionFile));

        // then
        assertThat(throwable).hasMessage("Decryption keys must include the encryption key.");
    }

    @Test
    void should_throw_exception_when_encryption_key_does_not_have_key_id() throws IOException {
        // given
        final File encryptionFile = temporaryFolder.resolve("st-enc-jwk.json").toFile();
        final File decryptionFile = temporaryFolder.resolve("st-dec-jwk.json").toFile();

        givenKeysInFile(encryptionFile, "{\"kty\":\"oct\",\"k\":\"2zUFSf5c0m_E1MjSSiS5iwZ9lzQY-9sqQXHnU1KqW8w\",\"alg\":\"dir\"}");
        givenKeysInFile(decryptionFile, "{\"kty\":\"oct\",\"k\":\"2zUFSf5c0m_E1MjSSiS5iwZ9lzQY-9sqQXHnU1KqW8w\",\"alg\":\"dir\"}", KEY_2);

        // when
        final Throwable throwable = catchThrowable(() -> KeysLoader.load(encryptionFile, decryptionFile));

        // then
        assertThat(throwable).hasMessage("Encryption key must include key ID (kid).");
    }

    @Test
    void should_throw_exception_when_decryption_keys_do_not_have_key_id() throws IOException {
        // given
        final File encryptionFile = temporaryFolder.resolve("st-enc-jwk.json").toFile();
        final File decryptionFile = temporaryFolder.resolve("st-dec-jwk.json").toFile();

        givenKeysInFile(encryptionFile, KEY_1);
        givenKeysInFile(decryptionFile, KEY_1, "{\"kty\":\"oct\",\"k\":\"Bsd2_f0XUx7mBscwqLvnwDk-cic-3UmMst65Ws1IUBc\",\"alg\":\"dir\"}");

        // when
        final Throwable throwable = catchThrowable(() -> KeysLoader.load(encryptionFile, decryptionFile));

        // then
        assertThat(throwable).hasMessage("All decryption keys must include key ID (kid).");
    }

    @Test
    void should_throw_exception_when_decryption_keys_have_duplicated_key_ids() throws IOException {
        // given
        final File encryptionFile = temporaryFolder.resolve("st-enc-jwk.json").toFile();
        final File decryptionFile = temporaryFolder.resolve("st-dec-jwk.json").toFile();

        givenKeysInFile(encryptionFile, KEY_1);
        givenKeysInFile(decryptionFile, KEY_1, "{\"kty\":\"oct\",\"kid\":\"test-key-1\",\"k\":\"Bsd2_f0XUx7mBscwqLvnwDk-cic-3UmMst65Ws1IUBc\",\"alg\":\"dir\"}");

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
