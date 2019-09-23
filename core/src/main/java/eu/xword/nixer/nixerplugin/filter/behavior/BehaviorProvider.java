package eu.xword.nixer.nixerplugin.filter.behavior;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import eu.xword.nixer.nixerplugin.filter.RequestAugmentation;
import eu.xword.nixer.nixerplugin.ip.IpMetadata;
import org.springframework.stereotype.Component;

@Component
public class BehaviorProvider {

    private ConcurrentHashMap<String, Behavior> behaviors = new ConcurrentHashMap<>();

    private List<Rule> rules = new ArrayList<>();

    {
        rules.add(new PredicateRule("blacklistedIp", attributes -> {
            IpMetadata ipMetadata = (IpMetadata) attributes.get(RequestAugmentation.IP_METADATA);
            return ipMetadata != null && ipMetadata.isBlacklisted();
        }));
        behaviors.put("blacklistedIp", new RedirectBehavior("/login?blockedError"));
    }

    public List<Behavior> get(Map<String, Object> attributes) {

        List<Behavior> result = new ArrayList<>();
        for (Rule r : rules) {
            if (r.condition(attributes)) {
                result.add(behaviors.get(r.name()));
            }
        }
        return result;
    }

    public void setBehavior(String name, Behavior behavior) {
        behaviors.put(name, behavior);
    }
}
