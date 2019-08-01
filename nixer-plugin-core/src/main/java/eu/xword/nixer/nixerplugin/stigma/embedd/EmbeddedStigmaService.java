package eu.xword.nixer.nixerplugin.stigma.embedd;

import java.util.UUID;

import com.nimbusds.jwt.JWT;
import eu.xword.nixer.nixerplugin.LoginResult;
import eu.xword.nixer.nixerplugin.LoginContext;
import eu.xword.nixer.nixerplugin.stigma.StigmaService;
import eu.xword.nixer.nixerplugin.stigma.StigmaToken;
import eu.xword.nixer.nixerplugin.stigma.storage.StigmaRepository;
import eu.xword.nixer.nixerplugin.stigma.token.StigmaTokenProvider;
import eu.xword.nixer.nixerplugin.stigma.token.validation.StigmaTokenValidator;
import eu.xword.nixer.nixerplugin.stigma.token.validation.ValidationResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

@Component
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
    public StigmaToken refreshStigma(StigmaToken receivedStigma, final LoginResult loginResult, final LoginContext loginContext) {
        logger.info("Found token: " + receivedStigma);

        StigmaToken stigma = null;

        if (receivedStigma != null) {
            final ValidationResult validationResult = stigmaTokenValidator.validate(receivedStigma.getValue());
            if (validationResult.isValid()) {
                stigma = receivedStigma;
            }
        }
        if (stigma == null) {
            stigma = fetchNewStigma();
        }

        stigmaRepository.save(stigma, loginResult);

        return stigma;
    }

    private StigmaToken fetchNewStigma() {
        final String stigma = UUID.randomUUID().toString();
        logger.debug("Creating new stigma " + stigma);
        final JWT token = stigmaTokenProvider.getToken(stigma);
        return new StigmaToken(token.serialize());
    }
}
