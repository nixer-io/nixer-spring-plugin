package eu.xword.nixer.nixerplugin.stigma.storage;

import java.util.concurrent.ConcurrentHashMap;

import eu.xword.nixer.nixerplugin.login.LoginResult;
import eu.xword.nixer.nixerplugin.stigma.StigmaToken;
import org.springframework.stereotype.Component;

@Component
public class InMemoryStigmaRepository implements StigmaRepository {

    private final ConcurrentHashMap<String, Integer> stigmas = new ConcurrentHashMap<>();

    @Override
    public void save(final StigmaToken stigma, final LoginResult loginResult) {
        final String stigmaValue = stigma.getValue();
        stigmas.putIfAbsent(stigmaValue, 0);
        stigmas.computeIfPresent(stigmaValue, (token, integer) -> loginResult.isSuccess() ? 0 : integer + 1);
    }
}
