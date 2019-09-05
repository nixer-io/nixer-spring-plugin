package eu.xword.nixer.nixerplugin.blocking;

import java.io.IOException;
import java.io.StringWriter;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import eu.xword.nixer.nixerplugin.blocking.events.BlockEvent;
import eu.xword.nixer.nixerplugin.blocking.events.BlockSourceIPEvent;
import eu.xword.nixer.nixerplugin.blocking.events.GlobalCredentialStuffingEvent;
import eu.xword.nixer.nixerplugin.blocking.events.LockUserEvent;

public class JsonSerializer implements EventVisitor {

    private static final JsonFactory jsonFactory = new JsonFactory();
    private final JsonGenerator generator;
    private final StringWriter stringWriter = new StringWriter();

    public JsonSerializer() {
        try {
            generator = jsonFactory.createGenerator(stringWriter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void accept(final BlockEvent event) {
        apply(event, this::nop);
    }

    @Override
    public void accept(final LockUserEvent event) {
        apply(event, () -> writeStringField("user", event.getUsername()));
    }

    @Override
    public void accept(final BlockSourceIPEvent event) {
        apply(event, () -> writeStringField("ip", event.getIp()));
    }

    private void writeStringField(String name, String value) {
        try {
            generator.writeStringField(name, value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void nop() {
    }

    private <T extends BlockEvent> void apply(T event, Runnable runnable) {
        try {
            generator.writeStartObject();
            generator.writeStringField("type", event.type());
            generator.writeNumberField("timestamp", event.getTimestamp());

            runnable.run();

            generator.writeEndObject();

            generator.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void accept(final GlobalCredentialStuffingEvent event) {
        accept((BlockEvent) event);
    }

    public String toString() {
        return stringWriter.toString();
    }
}
