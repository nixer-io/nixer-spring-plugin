package eu.xword.nixer.nixerplugin.filter.behavior;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.util.Assert;

@Endpoint(id = "behaviors")
public class BehaviorEndpoint {

    private BehaviorProvider behaviorProvider;

    private BehaviorRegistry behaviorRegistry;

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

    private Map<String, String> getRules() {
        return behaviorProvider.getRuleBehaviors();
    }

    private Set<String> geBehaviors() {
        return new TreeSet<>(behaviorRegistry.getBehaviors()).descendingSet();
    }

    @WriteOperation
    public void setBehavior(String rule, String behavior) {
        behaviorProvider.setBehavior(rule, behavior);
    }
}
