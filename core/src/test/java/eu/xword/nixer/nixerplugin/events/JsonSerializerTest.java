package eu.xword.nixer.nixerplugin.events;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JsonSerializerTest {

    @Test
    public void testSerializeGlobalCredentialStuffingEvent() {
        final JsonSerializer serializer = new JsonSerializer();
        final GlobalCredentialStuffingEvent event = new GlobalCredentialStuffingEvent();

        event.accept(serializer);

        assertThat(serializer.toString()).contains("\"type\":\"GLOBAL_CREDENTIAL_STUFFING\"");
    }

    @Test
    public void testSerializeBlockIpEvent() {
        final JsonSerializer serializer = new JsonSerializer();
        final BlockSourceIpEvent event = new BlockSourceIpEvent("127.0.0.1");

        event.accept(serializer);

        assertThat(serializer.toString()).contains("\"type\":\"BLOCK_SOURCE_IP\"");
        assertThat(serializer.toString()).contains("\"ip\":\"127.0.0.1\"");
    }
}