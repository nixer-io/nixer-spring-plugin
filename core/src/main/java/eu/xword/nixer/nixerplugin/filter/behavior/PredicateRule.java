package eu.xword.nixer.nixerplugin.filter.behavior;

import java.util.Map;
import java.util.function.Predicate;

public class PredicateRule implements Rule {

    private String name;
    private Predicate<Map<String, Object>> predicate;

    @Override
    public String name() {
        return name;
    }

    @Override
    public boolean condition(final Map<String, Object> attributes) {
        return predicate.test(attributes);
    }

    public PredicateRule(final String name, final Predicate<Map<String, Object>> predicate) {
        this.name = name;
        this.predicate = predicate;
    }
}
