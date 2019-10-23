package eu.xword.nixer.nixerplugin.core.filter.behavior;

public interface Rule {

    String name();

    boolean condition(Facts facts);
}
