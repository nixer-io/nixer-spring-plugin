package eu.xword.nixer.nixerplugin.filter.behavior;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class BehaviorRegistry {

    private Map<String, Behavior> behaviorByName = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        this
                .register(new CaptchaBehaviour())
                .register(new LogBehavior())
                .register(new PassthroughBehavior())
                .register(new RedirectBehavior("/login?blockedError", "blockedError"));
    }

    public Behavior findByName(String name) {
        Assert.notNull(name, "Name must not be null");

        return behaviorByName.get(name);
    }

    public BehaviorRegistry register(Behavior behavior) {
        Assert.notNull(behavior, "Behavior must not be null");
        String name = behavior.name();
        Assert.isTrue(!behaviorByName.containsKey(name), () -> "Behaviour with name " + name + " already registered");

        behaviorByName.put(name, behavior);

        return this;
    }

    public Set<String> getBehaviors() {
        return behaviorByName.keySet();
    }
}
