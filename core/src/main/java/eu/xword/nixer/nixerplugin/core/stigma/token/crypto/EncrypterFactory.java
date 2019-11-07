package eu.xword.nixer.nixerplugin.core.stigma.token.crypto;

import javax.crypto.SecretKey;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEEncrypter;

/**
 * Base class for factory of {@link JWEEncrypter}. Specifies encryption details such as algorithm, method and key.
 * Subclasses are supposed to define particular encrypter implementation.
 *
 * Created on 2019-05-22.
 *
 * @author gcwiak
 */
public abstract class EncrypterFactory {

    private final JWEAlgorithm algorithm;

    private final EncryptionMethod encryptionMethod;

    private final SecretKey key;

    private final String keyId;

    public EncrypterFactory(final JWEAlgorithm algorithm, final EncryptionMethod encryptionMethod, final SecretKey key, final String keyId) {
        this.algorithm = algorithm;
        this.encryptionMethod = encryptionMethod;
        this.key = key;
        this.keyId = keyId;
    }

    public JWEAlgorithm getAlgorithm() {
        return algorithm;
    }

    public EncryptionMethod getEncryptionMethod() {
        return encryptionMethod;
    }

    public final String getKeyId() {
        return keyId;
    }

    protected final SecretKey getKey() {
        return key;
    }

    public abstract JWEEncrypter encrypter();
}
