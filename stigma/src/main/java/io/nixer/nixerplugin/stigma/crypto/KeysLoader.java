package io.nixer.nixerplugin.stigma.crypto;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import org.springframework.util.Assert;

/**
 * Loads an encryption key and a set of decryption keys to be used during Stigma Token creation and validation.
 * Checks the encryption key is present among the decryption keys so the created tokens can be decrypted later during validation.
 * The other decryption keys are the previously used ones kept so it is possible to validate older tokens created before changing
 * the encryption key.
 *
 * Created on 2019-05-29.
 *
 * @author gcwiak
 */
@SuppressWarnings("rawtypes") // we don't use SecurityContext parameter for key source
public class KeysLoader {

    private final JWK encryptionKey;

    private final ImmutableJWKSet decryptionKeySet;

    private KeysLoader(final JWK encryptionKey, final ImmutableJWKSet decryptionKeySet) {
        Assert.notNull(encryptionKey, "encryptionKey must not be null");
        Assert.notNull(decryptionKeySet, "decryptionKeySet must not be null");
        this.encryptionKey = encryptionKey;
        this.decryptionKeySet = decryptionKeySet;
    }

    public static KeysLoader load(final File encryptionKeyFile, final File decryptionKeyFile) {
        Assert.notNull(encryptionKeyFile, "encryptionKeyFile must not be null");
        Assert.notNull(decryptionKeyFile, "decryptionKeyFile must not be null");

        final JWK encryptionKey = loadEncryptionKey(encryptionKeyFile);
        final ImmutableJWKSet decryptionKeySet = loadDecryptionKeys(decryptionKeyFile);

        validateKeys(encryptionKey, decryptionKeySet);

        return new KeysLoader(encryptionKey, decryptionKeySet);
    }

    private static void validateKeys(final JWK encryptionKey, final ImmutableJWKSet decryptionKeySet) {
        Assert.hasText(encryptionKey.getKeyID(), "Encryption key must include key ID (kid).");

        final List<JWK> decryptionKeys = decryptionKeySet.getJWKSet().getKeys();

        decryptionKeys.forEach(decryptionKey -> Assert.hasText(decryptionKey.getKeyID(), "All decryption keys must include key ID (kid)."));

        final long uniqueKeyIdsCount = decryptionKeys.stream().map(JWK::getKeyID).distinct().count();
        Assert.state(decryptionKeys.size() == uniqueKeyIdsCount, "Decryption keys must have unique key IDs.");

        Assert.state(decryptionKeys.contains(encryptionKey), "Decryption keys must include the encryption key.");
    }

    private static JWK loadEncryptionKey(final File encryptionKeyFile) {
        final JWKSet jwkSet = loadJwkSet(encryptionKeyFile);

        Assert.state(jwkSet.getKeys().size() == 1, "JWK encryption set must contain only one key");

        return jwkSet.getKeys().get(0);
    }

    private static ImmutableJWKSet loadDecryptionKeys(final File decryptionKeyFile) {
        final JWKSet jwkSet = loadJwkSet(decryptionKeyFile);
        return new ImmutableJWKSet(jwkSet);
    }

    private static JWKSet loadJwkSet(final File jwkFile) {
        try {
            return JWKSet.load(jwkFile);
        } catch (IOException | ParseException e) {
            throw new IllegalStateException("Failed to load JWK set from file: " + jwkFile, e);
        }
    }

    public JWK getEncryptionKey() {
        return encryptionKey;
    }

    public ImmutableJWKSet getDecryptionKeySet() {
        return decryptionKeySet;
    }
}
