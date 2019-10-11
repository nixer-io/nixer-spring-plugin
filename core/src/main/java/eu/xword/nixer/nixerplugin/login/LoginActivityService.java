package eu.xword.nixer.nixerplugin.login;

import java.util.List;

import eu.xword.nixer.nixerplugin.detection.rules.RulesRunner;
import org.springframework.stereotype.Component;

@Component
public class LoginActivityService {

    private final List<LoginActivityRepository> loginActivityRepositories;

    private final RulesRunner rulesRunner;

    public LoginActivityService(final List<LoginActivityRepository> loginActivityRepositories, final RulesRunner rulesRunner) {
        this.loginActivityRepositories = loginActivityRepositories;
        this.rulesRunner = rulesRunner;
    }

    public void handle(final LoginResult loginResult, final LoginContext context) {
        //TODO extract keeping track of stats to dedicated place
        loginActivityRepositories.forEach(it -> it.reportLoginActivity(loginResult, context));

        rulesRunner.onLogin(loginResult, context);
    }

}
