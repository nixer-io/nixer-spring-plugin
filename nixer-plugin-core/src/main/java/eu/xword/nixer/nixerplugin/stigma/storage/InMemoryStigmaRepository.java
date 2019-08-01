package eu.xword.nixer.nixerplugin.stigma.storage;

import java.util.concurrent.ConcurrentHashMap;

import eu.xword.nixer.nixerplugin.LoginResult;
import eu.xword.nixer.nixerplugin.stigma.StigmaToken;
import org.springframework.stereotype.Component;

import static eu.xword.nixer.nixerplugin.LoginResult.FAILURE;

@Component
public class InMemoryStigmaRepository implements StigmaRepository {

    private final ConcurrentHashMap<String, Integer> stigmas = new ConcurrentHashMap<>();

    @Override
    public void save(final StigmaToken stigma, final LoginResult loginResult) {
        final String stigmaValue = stigma.getValue();
        stigmas.putIfAbsent(stigmaValue, 0);
        stigmas.computeIfPresent(stigmaValue, (token, integer) -> loginResult == FAILURE ? integer + 1 : 0);
    }
}
