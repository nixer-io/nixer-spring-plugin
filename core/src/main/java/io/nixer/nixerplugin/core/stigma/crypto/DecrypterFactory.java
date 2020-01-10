package io.nixer.nixerplugin.core.stigma.crypto;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.JWEHeader;

/**
 * Base class for factory of {@link JWEDecrypter}. Specifies decryption details such as algorithm and method.
 * Subclasses are supposed to define particular decrypter implementation and obtain decryption key.
 *
 * Created on 2019-05-24.
 *
 * @author gcwiak
 */
public abstract class DecrypterFactory {

    private final JWEAlgorithm algorithm;

    private final EncryptionMethod encryptionMethod;

    public DecrypterFactory(final JWEAlgorithm algorithm, final EncryptionMethod encryptionMethod) {
        this.algorithm = algorithm;
        this.encryptionMethod = encryptionMethod;
    }

    public JWEAlgorithm getAlgorithm() {
        return algorithm;
    }

    public EncryptionMethod getEncryptionMethod() {
        return encryptionMethod;
    }

    public abstract JWEDecrypter decrypter(JWEHeader header);

}
