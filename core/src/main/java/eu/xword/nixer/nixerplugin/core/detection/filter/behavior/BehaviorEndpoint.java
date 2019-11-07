package eu.xword.nixer.nixerplugin.core.detection.filter.behavior;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.util.Assert;

/**
 * Actuator endpoint for managing behaviors and rules. It allows reading behaviors and rules configuration and changing behaviors for rules.
 */
@Endpoint(id = "behaviors")
public class BehaviorEndpoint {

    private final BehaviorProvider behaviorProvider;

    private final BehaviorRegistry behaviorRegistry;

    public BehaviorEndpoint(final BehaviorProvider behaviorProvider, final BehaviorRegistry behaviorRegistry) {
        Assert.notNull(behaviorProvider, "BehaviorProvider must not be null");
        this.behaviorProvider = behaviorProvider;

        Assert.notNull(behaviorRegistry, "BehaviorRegistry must not be null");
        this.behaviorRegistry = behaviorRegistry;
    }

    @ReadOperation
    public Map getBehaviors() {
        Map<String, Object> result = new HashMap<>();
        result.put("behaviors", geBehaviors());
        result.put("rules", getRules());

        return result;
    }

    @WriteOperation
    public void setBehavior(String rule, String behavior) {
        final Behavior newBehavior = behaviorRegistry.findByName(behavior);
        behaviorProvider.getRule(rule).updateBehavior(newBehavior);
    }

    private Map<String, String> getRules() {
        return behaviorProvider.getRules()
                .stream()
                .collect(Collectors.toMap(Rule::name, it -> it.behavior().name()));
    }

    private Collection<String> geBehaviors() {
        return behaviorRegistry.getBehaviors()
                .stream()
                .map(Behavior::name)
                .sorted()
                .collect(Collectors.toList());
    }

}
