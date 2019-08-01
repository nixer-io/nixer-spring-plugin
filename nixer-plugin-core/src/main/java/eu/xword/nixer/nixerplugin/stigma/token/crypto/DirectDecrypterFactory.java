package eu.xword.nixer.nixerplugin.stigma.token.crypto;

import java.security.Key;
import java.util.List;
import javax.crypto.SecretKey;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.JWEDecryptionKeySelector;
import com.nimbusds.jose.proc.JWEKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Factory of {@link DirectDecrypter} with decryption key selected from the provided keys source.
 *
 * Created on 2019-05-24.
 *
 * @author gcwiak
 */
@SuppressWarnings({"unchecked", "rawtypes"}) // we don't use SecurityContext parameter for key selector or source
public class DirectDecrypterFactory extends DecrypterFactory {

    private final Log logger = LogFactory.getLog(getClass());

    private static final JWEAlgorithm ALGORITHM = JWEAlgorithm.DIR;
    private static final EncryptionMethod ENCRYPTION_METHOD = EncryptionMethod.A128CBC_HS256;

    private final JWEKeySelector keySelector;


    public DirectDecrypterFactory(final JWKSource jwkSource) {
        super(ALGORITHM, ENCRYPTION_METHOD);

//        Preconditions.checkNotNull(jwkSource, "jwkSource");

        this.keySelector = new JWEDecryptionKeySelector(ALGORITHM, ENCRYPTION_METHOD, jwkSource);
    }

    public static DirectDecrypterFactory withKeysFrom(final KeysLoader keysLoader) {
//        Preconditions.checkNotNull(keysLoader, "keysLoader");

        return new DirectDecrypterFactory(keysLoader.getDecryptionKeySet());
    }

    @Override
    public JWEDecrypter decrypter(final JWEHeader header) {
//        Preconditions.checkNotNull(header, "header");

        final List<Key> keys = selectKeys(header);

        if (keys.isEmpty()) {
            throw new IllegalStateException("No keys found for header: " + header);
        }

        if (keys.size() > 1) {
            // TODO handle multiple keys
            logger.warn("More than one key found for header " + header);
        }

        final Key key = keys.get(0);

        if (!(key instanceof SecretKey)) {
            throw new IllegalStateException(String.format(
                    "Invalid key type for header '%s'. Expected '%s' but got '%s'",
                    header, SecretKey.class, key.getClass()
            ));
        }

        try {
            return new DirectDecrypter((SecretKey) key);
        } catch (KeyLengthException e) {
            throw new IllegalStateException("Could not create encrypter for header: " + header, e);
        }
    }

    private List<Key> selectKeys(final JWEHeader header) {
        try {
            return keySelector.selectJWEKeys(header, (SecurityContext) null);
        } catch (KeySourceException e) {
            throw new IllegalStateException("Could not select key for header: " + header, e);
        }
    }
}
