package eu.xword.nixer.nixerplugin.core.filter.behavior;

import java.util.function.Predicate;

public class PredicateRule implements Rule {

    private String name;
    private Predicate<Facts> predicate;

    @Override
    public String name() {
        return name;
    }

    @Override
    public boolean condition(final Facts facts) {
        return predicate.test(facts);
    }

    public PredicateRule(final String name, final Predicate<Facts> predicate) {
        this.name = name;
        this.predicate = predicate;
    }
}
