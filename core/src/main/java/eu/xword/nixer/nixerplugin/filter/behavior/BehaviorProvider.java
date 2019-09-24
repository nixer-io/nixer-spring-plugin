package eu.xword.nixer.nixerplugin.filter.behavior;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import eu.xword.nixer.nixerplugin.ip.IpMetadata;
import org.springframework.stereotype.Component;

import static eu.xword.nixer.nixerplugin.filter.RequestAugmentation.GLOBAL_CREDENTIAL_STUFFING;
import static eu.xword.nixer.nixerplugin.filter.RequestAugmentation.IP_METADATA;

@Component
public class BehaviorProvider {

    private ConcurrentHashMap<String, Behavior> behaviors = new ConcurrentHashMap<>();

    private List<Rule> rules = new ArrayList<>();

    {
        rules.add(new PredicateRule("blacklistedIp",
                facts -> {
                    IpMetadata ipMetadata = (IpMetadata) facts.getFact(IP_METADATA);
                    return ipMetadata != null && ipMetadata.isBlacklisted();
                }));
        rules.add(new PredicateRule("credentialStuffingActive",
                facts -> Boolean.TRUE.equals(facts.getFact(GLOBAL_CREDENTIAL_STUFFING))));


        behaviors.put("blacklistedIp", new RedirectBehavior("/login?blockedError"));
        behaviors.put("credentialStuffingActive", new CaptchaBehaviour());
    }

    public List<Behavior> get(Facts facts) {

        List<Behavior> result = new ArrayList<>();
        for (Rule r : rules) {
            if (r.condition(facts)) {
                result.add(behaviors.get(r.name()));
            }
        }
        return result;
    }

    public void setBehavior(String name, Behavior behavior) {
        behaviors.put(name, behavior);
    }
}
