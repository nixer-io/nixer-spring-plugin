package eu.xword.nixer.nixerplugin.core.detection.filter.behavior;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * Manages behaviors. Allowing to lookup by name and registering new behaviors.
 */
public class BehaviorRegistry implements InitializingBean {

    /**
     * We use mapping between rules and behaviors to make it possible to change behavior at runtime
     */
    private final Map<String, Behavior> behaviorByName = new ConcurrentHashMap<>();
    
    @Override
    public void afterPropertiesSet() {
        this
                .register(new PassthroughBehavior())
                .register(new RedirectBehavior("/login?blockedError", Behaviors.BLOCKED_ERROR.name()))
                .register(new RedirectBehavior("/login?error", Behaviors.BAD_CREDENTIALS_ERROR.name()));
    }

    public Behavior findByName(final String name) {
        Assert.notNull(name, "Name must not be null");

        return behaviorByName.get(name);
    }

    public BehaviorRegistry register(final Behavior behavior) {
        Assert.notNull(behavior, "Behavior must not be null");
        String name = behavior.name();
        Assert.isTrue(!behaviorByName.containsKey(name), () -> "Behaviour with name " + name + " already registered");

        behaviorByName.put(name, behavior);

        return this;
    }

    public Collection<Behavior> getBehaviors() {
        return behaviorByName.values();
    }
}
