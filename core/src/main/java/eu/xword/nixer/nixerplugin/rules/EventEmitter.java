package eu.xword.nixer.nixerplugin.rules;

import org.springframework.context.ApplicationEvent;

public interface EventEmitter {

    void emit(final ApplicationEvent event);
}
