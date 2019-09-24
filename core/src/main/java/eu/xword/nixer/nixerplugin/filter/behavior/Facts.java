package eu.xword.nixer.nixerplugin.filter.behavior;

import java.util.Map;

public class Facts {

    private final Map<String, Object> facts;

    public Facts(final Map<String, Object> facts) {
        this.facts = facts;
    }

    public Object getFact(String key) {
        return facts.get(key);
    }
}
