package io.nixer.nixerplugin.core.stigma.embed;

import java.util.UUID;

import com.nimbusds.jwt.JWT;
import io.nixer.nixerplugin.core.login.LoginResult;
import io.nixer.nixerplugin.core.stigma.StigmaService;
import io.nixer.nixerplugin.core.stigma.StigmaToken;
import io.nixer.nixerplugin.core.stigma.storage.StigmaRepository;
import io.nixer.nixerplugin.core.stigma.token.StigmaTokenProvider;
import io.nixer.nixerplugin.core.stigma.token.validation.StigmaTokenValidator;
import io.nixer.nixerplugin.core.stigma.token.validation.ValidationResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EmbeddedStigmaService implements StigmaService {

    private final Log logger = LogFactory.getLog(getClass());

    private StigmaRepository stigmaRepository;

    private StigmaTokenProvider stigmaTokenProvider;

    private StigmaTokenValidator stigmaTokenValidator;

    public EmbeddedStigmaService(final StigmaRepository stigmaRepository, final StigmaTokenProvider stigmaTokenProvider, final StigmaTokenValidator stigmaTokenValidator) {
        this.stigmaRepository = stigmaRepository;
        this.stigmaTokenProvider = stigmaTokenProvider;
        this.stigmaTokenValidator = stigmaTokenValidator;
    }

    @Override
    public StigmaToken refreshStigma(StigmaToken receivedStigma, final LoginResult loginResult) {
        logger.info("Found token: " + receivedStigma);

        if (receivedStigma != null) {
            final ValidationResult validationResult = stigmaTokenValidator.validate(receivedStigma.getValue());
            if (validationResult.isValid()) {
//                String stigmaValue = validationResult.getStigmaValue();
//                stigmaRepository.save(stigmaValue, loginResult);
                return receivedStigma;
            }
        }

        final String stigma = generateStigmaValue();
        stigmaRepository.save(stigma, loginResult);
        return tokenize(stigma);
    }

    private String generateStigmaValue() {
        final String stigma = UUID.randomUUID().toString();
        logger.debug("Creating new stigma " + stigma);
        return stigma;
    }

    private StigmaToken tokenize(String stigma) {
        final JWT token = stigmaTokenProvider.getToken(stigma);
        return new StigmaToken(token.serialize());
    }
}
