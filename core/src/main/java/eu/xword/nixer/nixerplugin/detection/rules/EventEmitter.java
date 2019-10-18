package eu.xword.nixer.nixerplugin.detection.rules;

import java.util.function.Consumer;

import eu.xword.nixer.nixerplugin.events.AnomalyEvent;

/**
 * Abstraction for event emitter
 */
public interface EventEmitter extends Consumer<AnomalyEvent> {

}
