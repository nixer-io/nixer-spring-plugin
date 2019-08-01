package eu.xword.nixer.nixerplugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.Duration;
import java.time.Instant;
import javax.annotation.Nonnull;

import eu.xword.nixer.nixerplugin.stigma.token.EncryptedStigmaTokenProvider;
import eu.xword.nixer.nixerplugin.stigma.token.PlainStigmaTokenProvider;
import eu.xword.nixer.nixerplugin.stigma.token.StigmaTokenConstants;
import eu.xword.nixer.nixerplugin.stigma.token.crypto.DirectDecrypterFactory;
import eu.xword.nixer.nixerplugin.stigma.token.crypto.DirectEncrypterFactory;
import eu.xword.nixer.nixerplugin.stigma.token.crypto.KeysLoader;
import eu.xword.nixer.nixerplugin.stigma.token.validation.EncryptedJwtValidator;
import eu.xword.nixer.nixerplugin.stigma.token.validation.StigmaTokenPayloadValidator;
import eu.xword.nixer.nixerplugin.stigma.token.validation.StigmaTokenValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import static io.micrometer.core.instrument.util.StringUtils.isNotBlank;

@Configuration
public class StigmaConfiguration {

    @Bean
    public StigmaTokenValidator buildStigmaTokenValidator(@Nonnull final EncryptedJwtValidator encryptedJwtValidator) {

        return new StigmaTokenValidator(encryptedJwtValidator);
    }

    @Bean
    public KeysLoader buildKeysLoader(final NixerProperties nixerProperties) throws FileNotFoundException {
        final StigmaProperties config = nixerProperties.getStigma();

        final File encryptionFile = ResourceUtils.getFile(config.getEncryptionKeyFile());
        final File decryptionFile = ResourceUtils.getFile(config.getDecryptionKeyFile());

        return KeysLoader.load(encryptionFile, decryptionFile);
    }

    @Bean
    public EncryptedJwtValidator buildEncryptedJwtValidator(final KeysLoader keysLoader,
                                                            final StigmaTokenPayloadValidator stigmaTokenPayloadValidator) {
        return new EncryptedJwtValidator(
                DirectDecrypterFactory.withKeysFrom(keysLoader),
                stigmaTokenPayloadValidator
        );
    }

    @Bean
    public StigmaTokenPayloadValidator buildStigmaTokenPayloadValidator(final NixerProperties nixerProperties) {
        final StigmaProperties stigmaProperties = nixerProperties.getStigma();
        final Duration tokenLifetime = isNotBlank(stigmaProperties.getTokenLifetime())
                ? Duration.parse(stigmaProperties.getTokenLifetime())
                : StigmaTokenConstants.DEFAULT_TOKEN_LIFETIME;

        return new StigmaTokenPayloadValidator(
                Instant::now,
                tokenLifetime
        );
    }

    @Bean
    public EncryptedStigmaTokenProvider buildEncryptedStigmaTokenProvider(final KeysLoader keysLoader) {

        return new EncryptedStigmaTokenProvider(
                new PlainStigmaTokenProvider(Instant::now),
                DirectEncrypterFactory.withKeysFrom(keysLoader)
        );
    }
}
