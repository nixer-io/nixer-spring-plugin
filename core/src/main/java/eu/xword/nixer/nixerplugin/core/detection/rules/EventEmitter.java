package eu.xword.nixer.nixerplugin.core.detection.rules;

import java.util.function.Consumer;

import eu.xword.nixer.nixerplugin.core.events.AnomalyEvent;

/**
 * Abstraction for event emitter
 */
public interface EventEmitter extends Consumer<AnomalyEvent> {

}
