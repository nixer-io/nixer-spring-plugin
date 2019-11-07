package io.nixer.nixerplugin.core.detection.events;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JsonSerializerTest {

    @Test
    void testSerializeGlobalCredentialStuffingEvent() {
        final JsonSerializer serializer = new JsonSerializer();
        final GlobalCredentialStuffingEvent event = new GlobalCredentialStuffingEvent();

        event.accept(serializer);

        assertThat(serializer.toString()).contains("\"type\":\"GLOBAL_CREDENTIAL_STUFFING\"");
    }

    @Test
    void testSerializeIpFailedLoginOverThresholdEvent() {
        final JsonSerializer serializer = new JsonSerializer();
        final IpFailedLoginOverThresholdEvent event = new IpFailedLoginOverThresholdEvent("127.0.0.1");

        event.accept(serializer);

        assertThat(serializer.toString()).contains("\"type\":\"IP_FAILED_LOGIN_OVER_THRESHOLD\"");
        assertThat(serializer.toString()).contains("\"ip\":\"127.0.0.1\"");
    }

    @Test
    void testSerializeUsernameFailedLoginOverThresholdEvent() {
        final JsonSerializer serializer = new JsonSerializer();
        final UsernameFailedLoginOverThresholdEvent event = new UsernameFailedLoginOverThresholdEvent("user1");

        event.accept(serializer);

        assertThat(serializer.toString()).contains("\"type\":\"USERNAME_FAILED_LOGIN_OVER_THRESHOLD\"");
        assertThat(serializer.toString()).contains("\"user\":\"user1\"");
    }

    @Test
    void testSerializeUserAgentFailedLoginOverThresholdEvent() {
        final JsonSerializer serializer = new JsonSerializer();
        final UserAgentFailedLoginOverThresholdEvent event = new UserAgentFailedLoginOverThresholdEvent("user-agent");

        event.accept(serializer);

        assertThat(serializer.toString()).contains("\"type\":\"USER_AGENT_FAILED_LOGIN_OVER_THRESHOLD\"");
        assertThat(serializer.toString()).contains("\"userAgent\":\"user-agent\"");
    }
}
