package io.nixer.nixerplugin.core.stigma.token.crypto;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

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

    public KeysLoader(final JWK encryptionKey, final ImmutableJWKSet decryptionKeySet) {
        Assert.notNull(encryptionKey, "JWK must not be null");
        this.encryptionKey = encryptionKey;
        Assert.notNull(decryptionKeySet, "ImmutableJWKSet must not be null");
        this.decryptionKeySet = decryptionKeySet;
    }

    public static KeysLoader load(final File encryptionKeyFile, final File decryptionKeyFile) {
        Assert.notNull(encryptionKeyFile, "File not be null");
        Assert.notNull(decryptionKeyFile, "File not be null");

        final JWK encryptionKey = loadEncryptionKey(encryptionKeyFile);
        final ImmutableJWKSet decryptionKeySet = loadDecryptionKeys(decryptionKeyFile);

        Assert.state(decryptionKeySet.getJWKSet().getKeys().contains(encryptionKey), "Decryption keys must include the encryption key.");

        return new KeysLoader(encryptionKey, decryptionKeySet);
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
