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
import io.nixer.nixerplugin.stigma.evaluate.StigmaService;
import io.nixer.nixerplugin.stigma.evaluate.StigmaValidator;
import io.nixer.nixerplugin.stigma.login.StigmaCookieService;
import io.nixer.nixerplugin.stigma.login.StigmaLoginActivityHandler;
import io.nixer.nixerplugin.stigma.storage.StigmaStorage;
import io.nixer.nixerplugin.stigma.storage.jdbc.JdbcDAOConfigurer;
import io.nixer.nixerplugin.stigma.storage.jdbc.StigmasJdbcDAO;
import io.nixer.nixerplugin.stigma.storage.jdbc.StigmasJdbcStorage;
import io.nixer.nixerplugin.stigma.generate.StigmaGenerator;
import io.nixer.nixerplugin.stigma.token.create.StigmaTokenFactory;
import io.nixer.nixerplugin.stigma.token.read.StigmaExtractor;
import io.nixer.nixerplugin.stigma.token.read.TokenDecrypter;
import io.nixer.nixerplugin.stigma.token.read.TokenParser;
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
    public KeysLoader buildKeysLoader(final StigmaProperties config) throws FileNotFoundException {

        final File encryptionFile = ResourceUtils.getFile(config.getEncryptionKeyFile());
        final File decryptionFile = ResourceUtils.getFile(config.getDecryptionKeyFile());

        return KeysLoader.load(encryptionFile, decryptionFile);
    }

    @Bean
    public TokenDecrypter tokenDecrypter(final KeysLoader keysLoader) {
        return new TokenDecrypter(DirectDecrypterFactory.withKeysFrom(keysLoader));
    }

    @Bean
    public TokenParser tokenParser() {
        return new TokenParser();
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
                                                       StigmaService stigmaService,
                                                       StigmaTokenFactory stigmaTokenFactory,
                                                       StigmaValidator stigmaValidator) {
        return new StigmaActionEvaluator(stigmaExtractor, stigmaService, stigmaTokenFactory, stigmaValidator);
    }

    @Bean
    public StigmaExtractor stigmaExtractor(TokenDecrypter tokenDecrypter, TokenParser tokenParser) {
        return new StigmaExtractor(tokenDecrypter, tokenParser);
    }

    @Bean
    public StigmaService stigmaService(StigmaStorage stigmaStorage,
                                       StigmaGenerator stigmaGenerator) {

        return new StigmaService(stigmaStorage, stigmaGenerator);
    }

    @Bean
    public StigmaValidator stigmaValidator(NowSource nowSource, StigmaProperties stigmaProperties) {
        return new StigmaValidator(nowSource, stigmaProperties.getStigmaLifetime());
    }

    @Bean
    public StigmaGenerator stigmaGenerator(NowSource nowSource) {
        return new StigmaGenerator(nowSource);
    }

    @Bean
    public StigmaStorage stigmaStorage(StigmasJdbcDAO stigmasJdbcDAO) {
        return new StigmasJdbcStorage(stigmasJdbcDAO);
    }
}
