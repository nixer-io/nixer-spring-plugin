package eu.xword.nixer.nixerplugin.core.stigma.storage;

import java.util.concurrent.ConcurrentHashMap;

import eu.xword.nixer.nixerplugin.core.login.LoginResult;

//@Repository
public class InMemoryStigmaRepository implements StigmaRepository {

    private final ConcurrentHashMap<String, Integer> stigmas = new ConcurrentHashMap<>();

    @Override
    public void save(final String stigma, final LoginResult loginResult) {
        stigmas.putIfAbsent(stigma, 0);
        stigmas.computeIfPresent(stigma, (token, integer) -> loginResult.isSuccess() ? 0 : integer + 1);
    }
}
