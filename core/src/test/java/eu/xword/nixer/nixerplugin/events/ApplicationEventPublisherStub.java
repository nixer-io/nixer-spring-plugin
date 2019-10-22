package eu.xword.nixer.nixerplugin.events;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;

public class ApplicationEventPublisherStub implements ApplicationEventPublisher {

    private final List<Object> events = new ArrayList<>();

    @Override
    public void publishEvent(final Object event) {
        events.add(event);
    }

    public List<Object> getEvents() {
        return events;
    }

    public void reset() {
        events.clear();
    }
}
