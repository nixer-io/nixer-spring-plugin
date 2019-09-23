package eu.xword.nixer.nixerplugin.filter.behavior;

import java.util.Map;

public interface Rule {

    String name();

    boolean condition(Map<String, Object> attributes);
}
