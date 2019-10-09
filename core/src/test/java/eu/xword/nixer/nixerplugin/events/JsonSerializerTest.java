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
    public void testSerializeIpFailedLoginOverThresholdEvent() {
        final JsonSerializer serializer = new JsonSerializer();
        final IpFailedLoginOverThresholdEvent event = new IpFailedLoginOverThresholdEvent("127.0.0.1");

        event.accept(serializer);

        assertThat(serializer.toString()).contains("\"type\":\"IP_FAILED_LOGIN_OVER_THRESHOLD\"");
        assertThat(serializer.toString()).contains("\"ip\":\"127.0.0.1\"");
    }
}