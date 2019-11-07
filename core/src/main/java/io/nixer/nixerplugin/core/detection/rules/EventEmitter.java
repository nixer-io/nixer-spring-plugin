package io.nixer.nixerplugin.core.detection.rules;

import java.util.function.Consumer;

import io.nixer.nixerplugin.core.detection.events.AnomalyEvent;

/**
 * Abstraction for event emitter
 */
public interface EventEmitter extends Consumer<AnomalyEvent> {

}
