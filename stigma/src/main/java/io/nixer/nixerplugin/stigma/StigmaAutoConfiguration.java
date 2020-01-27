package io.nixer.nixerplugin.stigma;

import java.io.File;
import java.io.FileNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import io.nixer.nixerplugin.core.NixerAutoConfiguration;
import io.nixer.nixerplugin.core.util.NowSource;
import io.nixer.nixerplugin.stigma.crypto.DirectDecrypterFactory;
import io.nixer.nixerplugin.stigma.crypto.DirectEncrypterFactory;
import io.nixer.nixerplugin.stigma.crypto.KeysLoader;
import io.nixer.nixerplugin.stigma.evaluate.StigmaActionEvaluator;
import io.nixer.nixerplugin.stigma.evaluate.StigmaTokenService;
import io.nixer.nixerplugin.stigma.evaluate.StigmaValidator;
import io.nixer.nixerplugin.stigma.login.StigmaCookieService;
import io.nixer.nixerplugin.stigma.login.StigmaLoginActivityHandler;
import io.nixer.nixerplugin.stigma.storage.StigmaTokenStorage;
import io.nixer.nixerplugin.stigma.storage.jdbc.JdbcDAOConfigurer;
import io.nixer.nixerplugin.stigma.storage.jdbc.StigmasJdbcDAO;
import io.nixer.nixerplugin.stigma.storage.jdbc.StigmasJdbcStorage;
import io.nixer.nixerplugin.stigma.token.StigmaExtractor;
import io.nixer.nixerplugin.stigma.token.StigmaTokenFactory;
import io.nixer.nixerplugin.stigma.token.StigmaValuesGenerator;
import io.nixer.nixerplugin.stigma.token.validation.EncryptedJwtValidator;
import io.nixer.nixerplugin.stigma.token.validation.StigmaTokenPayloadValidator;
import io.nixer.nixerplugin.stigma.token.validation.StigmaTokenValidator;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

@Configuration
@EnableConfigurationProperties(value = {StigmaProperties.class})
@AutoConfigureOrder(NixerAutoConfiguration.ORDER + 1)
public class StigmaAutoConfiguration {

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

    @Bean
    public StigmaTokenFactory stigmaTokenFactory(KeysLoader keysLoader) {

        return new StigmaTokenFactory(DirectEncrypterFactory.withKeysFrom(keysLoader));
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
    public StigmaTokenService stigmaTokenService(StigmaTokenFactory stigmaTokenFactory,
                                                 StigmaTokenStorage stigmaTokenStorage,
                                                 StigmaValuesGenerator stigmaValuesGenerator) {

        return new StigmaTokenService(stigmaTokenFactory, stigmaTokenStorage, stigmaValuesGenerator);
    }

    @Bean
    public StigmaValidator stigmaValidator(NowSource nowSource, StigmaProperties stigmaProperties) {
        return new StigmaValidator(nowSource, stigmaProperties.getStigmaLifetime());
    }

    @Bean
    public StigmaValuesGenerator stigmaValuesGenerator(NowSource nowSource) {
        return new StigmaValuesGenerator(nowSource);
    }

    @Bean
    public StigmaTokenStorage stigmaTokenStorage(StigmasJdbcDAO stigmasJdbcDAO) {
        return new StigmasJdbcStorage(stigmasJdbcDAO);
    }
}
