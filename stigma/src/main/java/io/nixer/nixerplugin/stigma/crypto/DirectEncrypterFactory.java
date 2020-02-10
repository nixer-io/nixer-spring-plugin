package io.nixer.nixerplugin.stigma.crypto;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import org.springframework.util.Assert;

/**
 * Factory of {@link DirectEncrypter} with shared symmetric key.
 *
 * Created on 2019-05-22.
 *
 * @author gcwiak
 */
public class DirectEncrypterFactory extends EncrypterFactory {

    public DirectEncrypterFactory(final OctetSequenceKey symmetricKey) {
        super(JWEAlgorithm.DIR, EncryptionMethod.A128CBC_HS256, symmetricKey.toSecretKey(), symmetricKey.getKeyID());
    }

    public static DirectEncrypterFactory withKeysFrom(final KeysLoader keysLoader) {
        Assert.notNull(keysLoader, "KeysLoader must not be null");

        final JWK encryptionKey = keysLoader.getEncryptionKey();
        Assert.isInstanceOf(OctetSequenceKey.class, encryptionKey,
                () -> "JWK must be an OctetSequenceKey, instead got: " + encryptionKey.getClass());

        return new DirectEncrypterFactory((OctetSequenceKey) encryptionKey);
    }

    @Override
    public JWEEncrypter encrypter() {
        try {
            return new DirectEncrypter(this.getKey());
        } catch (KeyLengthException e) {
            throw new IllegalStateException("Could not create encrypter for keyID: " + this.getKeyId(), e);
        }
    }
}
