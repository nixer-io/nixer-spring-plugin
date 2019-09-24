package eu.xword.nixer.nixerplugin.stigma;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.Duration;
import java.time.Instant;
import javax.annotation.Nonnull;

import eu.xword.nixer.nixerplugin.stigma.embed.EmbeddedStigmaService;
import eu.xword.nixer.nixerplugin.stigma.storage.StigmaRepository;
import eu.xword.nixer.nixerplugin.stigma.token.EncryptedStigmaTokenProvider;
import eu.xword.nixer.nixerplugin.stigma.token.PlainStigmaTokenProvider;
import eu.xword.nixer.nixerplugin.stigma.token.StigmaTokenConstants;
import eu.xword.nixer.nixerplugin.stigma.token.StigmaTokenProvider;
import eu.xword.nixer.nixerplugin.stigma.token.crypto.DirectDecrypterFactory;
import eu.xword.nixer.nixerplugin.stigma.token.crypto.DirectEncrypterFactory;
import eu.xword.nixer.nixerplugin.stigma.token.crypto.KeysLoader;
import eu.xword.nixer.nixerplugin.stigma.token.validation.EncryptedJwtValidator;
import eu.xword.nixer.nixerplugin.stigma.token.validation.StigmaTokenPayloadValidator;
import eu.xword.nixer.nixerplugin.stigma.token.validation.StigmaTokenValidator;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

@Configuration
@EnableConfigurationProperties(value = {StigmaProperties.class})
public class StigmaConfiguration {

    @Bean
    public StigmaTokenValidator buildStigmaTokenValidator(@Nonnull final EncryptedJwtValidator encryptedJwtValidator) {

        return new StigmaTokenValidator(encryptedJwtValidator);
    }

    @Bean
    public KeysLoader buildKeysLoader(final StigmaProperties config) throws FileNotFoundException {

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
    public StigmaTokenPayloadValidator buildStigmaTokenPayloadValidator(final StigmaProperties stigmaProperties) {
        final Duration tokenLifetime = !StringUtils.isEmpty(stigmaProperties.getTokenLifetime())
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

    @Bean
    public EmbeddedStigmaService stigmaService(StigmaRepository stigmaRepository, StigmaTokenProvider tokenProvider, StigmaTokenValidator tokenValidator) {
        return new EmbeddedStigmaService(stigmaRepository, tokenProvider, tokenValidator);
    }
}
