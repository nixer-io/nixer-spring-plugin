package io.nixer.nixerplugin.core.stigma;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.Duration;
import java.time.Instant;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import io.nixer.nixerplugin.core.stigma.crypto.DirectDecrypterFactory;
import io.nixer.nixerplugin.core.stigma.crypto.DirectEncrypterFactory;
import io.nixer.nixerplugin.core.stigma.crypto.KeysLoader;
import io.nixer.nixerplugin.core.stigma.evaluate.StigmaActionEvaluator;
import io.nixer.nixerplugin.core.stigma.evaluate.StigmaTokenService;
import io.nixer.nixerplugin.core.stigma.evaluate.StigmaValidator;
import io.nixer.nixerplugin.core.stigma.login.StigmaCookieService;
import io.nixer.nixerplugin.core.stigma.login.StigmaLoginActivityHandler;
import io.nixer.nixerplugin.core.stigma.storage.StigmaTokenStorage;
import io.nixer.nixerplugin.core.stigma.storage.jdbc.JdbcDAOConfigurer;
import io.nixer.nixerplugin.core.stigma.storage.jdbc.StigmasJdbcDAO;
import io.nixer.nixerplugin.core.stigma.storage.jdbc.StigmasJdbcStorage;
import io.nixer.nixerplugin.core.stigma.token.EncryptedStigmaTokenProvider;
import io.nixer.nixerplugin.core.stigma.token.PlainStigmaTokenProvider;
import io.nixer.nixerplugin.core.stigma.token.StigmaExtractor;
import io.nixer.nixerplugin.core.stigma.token.StigmaTokenConstants;
import io.nixer.nixerplugin.core.stigma.token.StigmaTokenProvider;
import io.nixer.nixerplugin.core.stigma.token.StigmaValuesGenerator;
import io.nixer.nixerplugin.core.stigma.token.validation.EncryptedJwtValidator;
import io.nixer.nixerplugin.core.stigma.token.validation.StigmaTokenPayloadValidator;
import io.nixer.nixerplugin.core.stigma.token.validation.StigmaTokenValidator;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

@Configuration
@EnableConfigurationProperties(value = {StigmaProperties.class})
public class StigmaConfiguration {

    @Bean
    public StigmasJdbcDAO stigmasJdbcDAO(DataSource dataSource) {
        final StigmasJdbcDAO jdbcDAO = new StigmasJdbcDAO();
        jdbcDAO.setDataSource(dataSource);
        return jdbcDAO;
    }

    @Bean
    public JdbcDAOConfigurer JdbcDAOConfigurer(DataSource dataSource) {
        return new JdbcDAOConfigurer(dataSource);
    }

    @Bean
    public StigmaCookieService stigmaCookieService(StigmaProperties stigmaProperties) {
        return new StigmaCookieService(stigmaProperties.getCookieName());
    }

    @Bean
    public StigmaTokenValidator buildStigmaTokenValidator(final EncryptedJwtValidator encryptedJwtValidator) {

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
    public StigmaTokenPayloadValidator stigmaTokenPayloadValidator() {
        return new StigmaTokenPayloadValidator();
    }

    @Primary
    @Bean
    public EncryptedStigmaTokenProvider encryptedStigmaTokenProvider(KeysLoader keysLoader,
                                                                     PlainStigmaTokenProvider plainStigmaTokenProvider) {

        return new EncryptedStigmaTokenProvider(
                plainStigmaTokenProvider,
                DirectEncrypterFactory.withKeysFrom(keysLoader)
        );
    }

    @Bean
    public PlainStigmaTokenProvider plainStigmaTokenProvider() {
        return new PlainStigmaTokenProvider();
    }

    @Bean
    public StigmaLoginActivityHandler stigmaLoginActivityHandler(HttpServletRequest request,
                                                                 HttpServletResponse response,
                                                                 StigmaCookieService stigmaCookieService,
                                                                 StigmaActionEvaluator stigmaActionEvaluator) {

        return new StigmaLoginActivityHandler(request, response, stigmaCookieService, stigmaActionEvaluator);
    }

    @Bean
    public StigmaActionEvaluator stigmaActionEvaluator(StigmaExtractor stigmaExtractor,
                                                       StigmaTokenService stigmaTokenService,
                                                       StigmaValidator stigmaValidator) {
        return new StigmaActionEvaluator(stigmaExtractor, stigmaTokenService, stigmaValidator);
    }

    @Bean
    public StigmaExtractor stigmaExtractor(StigmaTokenValidator stigmaTokenValidator) {
        return new StigmaExtractor(stigmaTokenValidator);
    }

    @Bean
    public StigmaTokenService stigmaTokenService(StigmaTokenProvider stigmaTokenProvider,
                                                 StigmaTokenStorage stigmaTokenStorage,
                                                 StigmaValuesGenerator stigmaValuesGenerator) {

        return new StigmaTokenService(stigmaTokenProvider, stigmaTokenStorage, stigmaValuesGenerator);
    }

    @Bean
    public StigmaValidator stigmaValidator(StigmaProperties stigmaProperties) {
        final Duration stigmaLifetime = !StringUtils.isEmpty(stigmaProperties.getTokenLifetime())
                ? Duration.parse(stigmaProperties.getTokenLifetime())
                : StigmaTokenConstants.DEFAULT_STIGMA_LIFETIME;

        return new StigmaValidator(Instant::now, stigmaLifetime);
    }

    @Bean
    public StigmaValuesGenerator stigmaValuesGenerator() {
        return new StigmaValuesGenerator(Instant::now);
    }

    @Bean
    public StigmaTokenStorage stigmaTokenStorage(StigmasJdbcDAO stigmasJdbcDAO) {
        return new StigmasJdbcStorage(stigmasJdbcDAO);
    }
}
