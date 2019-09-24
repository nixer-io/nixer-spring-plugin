package eu.xword.nixer.nixerplugin.filter.behavior;

public interface Rule {

    String name();

    boolean condition(Facts facts);
}
